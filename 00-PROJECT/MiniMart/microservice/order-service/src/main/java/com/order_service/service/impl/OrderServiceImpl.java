package com.order_service.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.finance.payment.PaymentSucceededEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.shared.dto.AllocationItemSnapshot;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.common_kafka.exception.UnhandledEventException;
import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.req.OrderFilter;
import com.order_service.dto.req.OrderUpdateReq;
import com.order_service.dto.res.OrderAdminSummaryDTO;
import com.order_service.dto.res.OrderDTO;
import com.order_service.dto.res.OrderDetailDTO;
import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentStatus;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.exception.ErrorCode;
import com.order_service.exception.OrderEventNotFoundException;
import com.order_service.mapper.OrderMapper;
import com.order_service.model.Order;
import com.order_service.model.OrderItem;
import com.order_service.repository.OrderItemRepository;
import com.order_service.repository.OrderRepository;
import com.order_service.repository.ShippingMethodRepository;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    private final OrderItemRepository itemRepository;

    private final ShippingMethodRepository shippingMethodRepository;

    private final OrderMapper mapper;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Page<OrderDTO> findPage(Pageable pageable, Long userId) {
        return repository.findByUserIdForCLientSummary(userId, pageable)
                .map(mapper::toClientSummaryDTO);
    }

    @Transactional(readOnly = true)
    @Override
    public OrderDetailDTO findByIdAndUserId(long id, Long userId) {

        Order order = repository.findByIdAndUserIdForClientDetail(id, userId)
                .orElseThrow(() -> new GenericException(ErrorCode.ORDER_NOT_FOUND, id));

        List<OrderItem> items = itemRepository.findByOrderId(id);

        order.setOrderItems(items);

        return mapper.toClientDetailDTO(order);
    }

    @Transactional
    @Override
    public OrderDTO create(OrderCreateReq req, Long userId) {
        Order order = mapper.toEntity(req);
        order.setStatus(OrderStatus.VALIDATING);
        order.setPaymentStatus(PaymentStatus.PREPARING);
        order.setUserId(userId);
        order.setShippingMethod(shippingMethodRepository.getReferenceById(req.getShippingMethodId()));
        repository.save(order);

        eventPublisher.publishEvent(new OrderPreparedDomainEvent(order));

        return mapper.toClientSummaryDTO(order);
    }

    @Override
    public OrderDTO createFromCart(OrderCreateReq req, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createFromCart'");
    }

    @Transactional
    @Override
    public void cancel(Long orderId, Long userId) {
        // Check: tồn tại và sở hữu đơn hàng
        Order order = repository.findByIdAndUserIdWithItem(orderId, userId)
                .orElseThrow(() -> new GenericException(ErrorCode.ORDER_NOT_FOUND, orderId));

        if (OrderStatus.isOrderEnded(order)) {
            throw new GenericException(ErrorCode.ORDER_CANCEL_LIFECYCLE_ENDED, orderId);
        }

        if (OrderStatus.isCancellingOrAwaiting(order)) {
            throw new GenericException(ErrorCode.ORDER_CANCEL_ALREADY_LOCKED, orderId);
        }

        if (!OrderStatus.isEligibleForNewCancelRequest(order)) {
            throw new GenericException(ErrorCode.ORDER_CANCEL_STATUS_INVALID, orderId);
        }

        order.setStatus(OrderStatus.AWAITING_CANCEL);

        repository.save(order);

        return;
    }

    @Override
    public Page<OrderAdminSummaryDTO> findAdminAll(OrderFilter filter, Pageable pageable) {
        return repository.findAll((root, query, builder) -> builder.conjunction(), pageable)
                .map(mapper::toAdminSummaryDTO);
    }

    @Override
    public OrderDTO cancelAdmin(long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cancelAdmin'");
    }

    @Override
    public OrderDTO updateStatus(long id, OrderUpdateReq req) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateStatus'");
    }

    @Transactional
    public Order processProductValidationPassedEvent(ProductValidationPassedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        if (order.getStatus() != OrderStatus.VALIDATING) {
            throw new IllegalStateException("Order is not validating");
        }

        Map<Long, ValidatedItemSnapshot> mapUnitPriceByProductId = event.getValidatedItems()
                .stream()
                .collect(Collectors.toMap(ValidatedItemSnapshot::getProductId, Function.identity()));

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem orderItem : order.getOrderItems()) {
            Long productId = orderItem.getProductId();
            ValidatedItemSnapshot vi = mapUnitPriceByProductId.get(productId);
            if (vi != null && vi.getQuantity() == orderItem.getQuantity()) {
                BigDecimal unitPrice = vi.getUnitPrice();
                orderItem.setUnitPrice(unitPrice);
                total = total.add(unitPrice.multiply(BigDecimal.valueOf(orderItem.getQuantity())));
            }
        }

        total = total.add(order.getShippingMethod().getCost());

        order.setTotalPrice(total);
        repository.save(order);
        return order;
    }

    @Transactional
    @Override
    public Order processInventoryReservedConfirmed(InventoryReservedConfirmedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        if (order.getStatus() != OrderStatus.VALIDATING) {
            throw new IllegalStateException("Order is not validating");
        }

        order.setStatus(OrderStatus.CONFIRMED);
        return order;

    }

    @Override
    public Order processPaymentRecordCreated(PaymentRecordPreparedEvent event) {
        long userId = event.getUserId();
        long orderId = event.getOrderId();
        long paymentId = event.getPaymentId();
        BigDecimal amountToPay = event.getAmountToPay();

        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        if (order.getTotalPrice().equals(amountToPay)) {
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setPaymentId(paymentId);

            repository.save(order);
        } else {
            throw new IllegalStateException("Order total Amount has changed");
        }

        return order;
    }

    @Transactional
    @Override
    public Order processInventoryAllocationConfirmed(InventoryAllocationConfirmedEvent event) {

        long userId = event.getUserId();
        long orderId = event.getOrderId();
        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        Map<Long, AllocationItemSnapshot> mapItemByProductId = event
                .getAllocatedItems()
                .stream()
                .collect(Collectors.toMap(AllocationItemSnapshot::getProductId, Function.identity()));

        BigDecimal totalCost = BigDecimal.ZERO;

        for (OrderItem oi : order.getOrderItems()) {
            long productId = oi.getProductId();
            AllocationItemSnapshot ais = mapItemByProductId.get(productId);
            if (ais != null) {
                totalCost = totalCost.add(ais.getAvgCostPrice());
                oi.setAvgCostPrice(ais.getAvgCostPrice());
            }

        }

        order.setTotalCost(totalCost);
        repository.save(order);

        return order;
    }

    @Transactional
    @Override
    public Order processOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        order.setStatus(OrderStatus.CREATION_FAILED);

        repository.save(order);
        return order;
    }

    @Transactional
    @Override
    public void processPaymentSucceedEvent(PaymentSucceededEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        if (order.getPaymentId() != event.getPaymentId()) {
            throw new UnhandledEventException("Update PaymentStatus", orderId,
                    "order.paymentId != event.getPaymentId()");
        }

        order.setPaymentStatus(PaymentStatus.PAID);

        repository.save(order);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order processCanceled(long orderId, long userId) {
        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        order.setStatus(OrderStatus.CANCELED);

        return order;

    }

}
