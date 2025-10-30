package com.order_service.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.common_kafka.event.finance.payment.PaymentPaidEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.finance.payment.PaymentRefundedEvent;
import com.common_kafka.event.shared.dto.AllocationItemSnapshot;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.common_kafka.exception.saga.BusinessInvariantViolationException;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.req.OrderFilter;
import com.order_service.dto.req.OrderUpdateReq;
import com.order_service.dto.res.OrderAdminSummaryDTO;
import com.order_service.dto.res.OrderDTO;
import com.order_service.dto.res.OrderDetailDTO;
import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;
import com.order_service.event.OrderCancelDomainEvent;
import com.order_service.event.OrderCreationFailedDomainEvent;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.exception.ErrorCode;
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

    @Override
    public Page<OrderAdminSummaryDTO> findAdminAll(OrderFilter filter, Pageable pageable) {
        return repository.findAll((root, query, builder) -> builder.conjunction(), pageable)
                .map(mapper::toAdminSummaryDTO);
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
        Order order = repository.findByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new GenericException(ErrorCode.ORDER_NOT_FOUND, orderId));
        cancel(order);
        return;
    }

    @Transactional
    @Override
    public OrderDTO cancelAdmin(long id) {
        Order order = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new GenericException(ErrorCode.ORDER_NOT_FOUND, id));

        order = cancel(order);

        return mapper.toClientSummaryDTO(order);
    }

    private Order cancel(Order order) {
        OrderStatus currentStatus = order.getStatus();
        Set<OrderStatus> cancelAbleStatuses = Set.of(
                OrderStatus.VALIDATING,
                OrderStatus.CONFIRMED,
                OrderStatus.ALLOCATED);

        if (!cancelAbleStatuses.contains(currentStatus)) {
            throw new GenericException(ErrorCode.ORDER_CANCEL_STATUS_INVALID, order.getId());
        }

        order.setStatus(OrderStatus.CANCELING);
        eventPublisher.publishEvent(new OrderCancelDomainEvent(order));

        repository.save(order);
        return order;
    }

    @Transactional
    @Override
    public OrderDTO updateStatus(long id, OrderUpdateReq req) {
        Order order = repository.findByIdForUpdate(id)
                .orElseThrow(() -> new GenericException(ErrorCode.ORDER_NOT_FOUND, id));

        OrderStatus newStatus = OrderStatus.valueOf(req.getStatus());
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.ALLOCATED) {
            if (newStatus != OrderStatus.SHIPPING) {
                throw new GenericException(ErrorCode.ORDER_UPDATE_STATUS_STEP_INVALID, id, currentStatus,
                        OrderStatus.SHIPPING);
            }
        } else if (currentStatus == OrderStatus.SHIPPING) {
            if (newStatus != OrderStatus.COMPLETED) {
                throw new GenericException(ErrorCode.ORDER_UPDATE_STATUS_STEP_INVALID, id, currentStatus,
                        OrderStatus.COMPLETED);
            }
        } else {
            throw new GenericException(ErrorCode.ORDER_UPDATE_STATUS_INVALID, id);

        }

        if (newStatus == OrderStatus.SHIPPING) {
            if (order.getPaymentMethod() == PaymentMethod.VNPAY && order.getPaymentStatus() == PaymentStatus.PAID) {
                throw new GenericException(ErrorCode.ORDER_UPDATE_STATUS_PAYMENT);
            }
        }
        order.setStatus(newStatus);
        repository.save(order);

        if (order.getStatus() == OrderStatus.COMPLETED) {
            eventPublisher.publishEvent(order);
        }

        return mapper.toClientSummaryDTO(order);
    }

    /**
     * Tiến hành cập nhật Unit Price + total Price sẵn sàng cho tạo PaymentRecord
     */

    @Transactional
    public Order processProductValidationPassedEvent(ProductValidationPassedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        if (order.getStatus() != OrderStatus.VALIDATING) {
            throw InvalidStateException.of("Order", orderId,
                    order.getStatus().name(), OrderStatus.VALIDATING.name());
        }

        if (order.getTotalPrice().compareTo(BigDecimal.ZERO) != 0) {
            throw IdempotentEventException.of("Order", orderId, Instant.now());
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

        Order order = repository.findByIdAndUserIdForUpdate(orderId, event.getUserId())
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        if (order.getStatus() != OrderStatus.VALIDATING) {
            throw InvalidStateException.of("Order", orderId, order.getStatus().name(), OrderStatus.VALIDATING.name());
        }

        repository.save(order);
        return order;

    }

    @Transactional
    @Override
    public Order processPaymentRecordCreated(PaymentRecordPreparedEvent event) {
        long userId = event.getUserId();
        long orderId = event.getOrderId();
        long paymentId = event.getPaymentId();
        BigDecimal amountToPay = event.getAmountToPay();

        Order order = repository.findByIdAndUserIdWithItem(orderId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        if (order.getStatus() != OrderStatus.VALIDATING) {
            throw InvalidStateException.of("Order", orderId, order.getStatus().name(), OrderStatus.VALIDATING.name());
        }

        if (order.getPaymentId() != null) {
            throw IdempotentEventException.of("Order", orderId, Instant.now());
        }

        if (order.getTotalPrice().compareTo(amountToPay) != 0) {
            throw new BusinessInvariantViolationException("Order", order.getId(), "Order total Amount has changed");
        }
        order.setStatus(OrderStatus.CONFIRMED);

        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setPaymentId(paymentId);

        repository.save(order);

        return order;
    }

    @Transactional
    @Override
    public Order processInventoryAllocationConfirmed(InventoryAllocationConfirmedEvent event) {
        long userId = event.getUserId();
        long orderId = event.getOrderId();

        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        if (order.getStatus() != OrderStatus.CONFIRMED) {
            throw InvalidStateException.of(
                    "Order",
                    orderId,
                    order.getStatus().name(),
                    OrderStatus.CONFIRMED.name());
        }

        if (order.getTotalCost().compareTo(BigDecimal.ZERO) != 0) {
            throw IdempotentEventException.of("Order", orderId, Instant.now());
        }

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

        order.setStatus(OrderStatus.ALLOCATED);
        order.setTotalCost(totalCost);
        repository.save(order);

        return order;
    }

    @Transactional
    @Override
    public Order processOrderCreationCompensated(long orderId, long userId) {

        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("order", orderId));

        if (order.getStatus() == OrderStatus.CREATION_FAILED) {
            throw new IdempotentEventException("Order", orderId, Instant.now());
        }

        if (order.getStatus() != OrderStatus.VALIDATING) {
            throw InvalidStateException.of(
                    "Order",
                    orderId,
                    order.getStatus().name(),
                    OrderStatus.VALIDATING.name());
        }

        order.setStatus(OrderStatus.CREATION_FAILED);
        repository.save(order);

        eventPublisher.publishEvent(new OrderCreationFailedDomainEvent(order));
        return order;
    }

    @Transactional
    @Override
    public void processPaymentPaidEvent(PaymentPaidEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        if (order.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new InvalidStateException(
                    "Order",
                    orderId,
                    order.getPaymentStatus().name(),
                    PaymentStatus.PENDING.name());
        }

        order.setPaymentStatus(PaymentStatus.PAID);

        repository.save(order);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order processCanceled(long orderId, long userId) {
        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> ResourceNotFoundException.of("Order", orderId));

        order.setStatus(OrderStatus.CANCELED);

        return order;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void processPaymentRefunded(PaymentRefundedEvent event) {
        Order order = repository.findByIdAndUserIdForUpdate(event.getOrderId(), event.getUserId())
                .orElseThrow(() -> ResourceNotFoundException.of("Order", event.getOrderId()));

        if (order.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw IdempotentEventException.of("Order", order.getId(), Instant.now());
        }

        order.setPaymentStatus(PaymentStatus.REFUNDED);

        repository.save(order);
    }

}
