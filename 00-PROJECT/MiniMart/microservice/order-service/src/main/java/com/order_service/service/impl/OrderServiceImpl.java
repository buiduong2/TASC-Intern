package com.order_service.service.impl;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.shared.dto.AllocationItemSnapshot;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.req.OrderFilter;
import com.order_service.dto.req.OrderUpdateReq;
import com.order_service.dto.res.OrderAdminDTO;
import com.order_service.dto.res.OrderDTO;
import com.order_service.dto.res.OrderDetailDTO;
import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentStatus;
import com.order_service.enums.SagaStepType;
import com.order_service.exception.OrderEventNotFoundException;
import com.order_service.mapper.OrderMapper;
import com.order_service.model.Order;
import com.order_service.model.OrderItem;
import com.order_service.repository.OrderRepository;
import com.order_service.saga.OrderSagaManager;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    private final OrderMapper mapper;

    private final OrderSagaManager sagaManager;

    private final OrderSagaTrackerService sagaTrackerService;

    @Override
    public Page<OrderDTO> findPage(Pageable pageable, Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPage'");
    }

    @Override
    public OrderDetailDTO findByIdAndUserId(long id, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByIdAndUserId'");
    }

    @Transactional
    @Override
    public OrderDTO create(OrderCreateReq req, Long userId) {
        Order order = mapper.toEntity(req);
        order.setStatus(OrderStatus.PENDING_VALIDATION);
        order.setPaymentStatus(PaymentStatus.PREPARING);
        order.setUserId(userId);
        repository.save(order);

        sagaTrackerService.create(order.getId());
        sagaManager.publishOrderCreationRequestedEvent(order);
        return mapper.toClientSummaryDTO(order);
    }

    @Override
    public OrderDTO createFromCart(OrderCreateReq req, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createFromCart'");
    }

    @Override
    public void cancel(Long orderId, Long userId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cancel'");
    }

    @Override
    public Page<OrderAdminDTO> findAdminAll(OrderFilter filter, Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAdminAll'");
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
    @Override
    public void processProductValidationFailed(ProductValidationFailedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        sagaTrackerService.markFailedStep(orderId, SagaStepType.UNIT_PRICE_CONFIRMED, event.getReason());
        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        order.setStatus(OrderStatus.PENDING_COMPENSATION_CREATION);

        repository.save(order);

        sagaManager.checkAndAdvanceOrder(order);
    }

    @Transactional
    public void processProductValidationPassedEvent(ProductValidationPassedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        commitPriceAndItems(order, event.getValidatedItems());

        sagaTrackerService.markSuccessStep(orderId, SagaStepType.UNIT_PRICE_CONFIRMED);

        sagaManager.publishOrderPriceCommittedEvent(order);
    }

    private void commitPriceAndItems(Order order, Set<ValidatedItemSnapshot> validatedItems) {
        Map<Long, ValidatedItemSnapshot> mapUnitPriceByProductId = validatedItems.stream()
                .collect(Collectors.toMap(ValidatedItemSnapshot::getProductId, Function.identity()));

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem orderItem : order.getOrderItems()) {
            Long productId = orderItem.getProductId();
            ValidatedItemSnapshot vi = mapUnitPriceByProductId.get(productId);
            if (vi != null && vi.getQuantity() == orderItem.getQuantity()) {
                BigDecimal unitPrice = vi.getUnitPrice();
                orderItem.setUnitPrice(unitPrice);
                total = total.add(unitPrice);
            }
        }

        total = total.add(order.getShippingMethod().getCost());

        order.setTotal(total);
        repository.save(order);

    }

    @Transactional
    @Override
    public void processInventoryReservedConfirmed(InventoryReservedConfirmedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();

        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        sagaTrackerService.markSuccessStep(orderId, SagaStepType.STOCK_RESERVED);
        sagaManager.checkAndAdvanceOrder(order);

    }

    @Transactional
    @Override
    public void processInventoryReservationFailed(InventoryReservationFailedEvent event) {
        long orderId = event.getOrderId();
        long userId = event.getUserId();
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        order.setStatus(OrderStatus.PENDING_COMPENSATION_CREATION);

        repository.save(order);

        sagaManager.checkAndAdvanceOrder(order);
    }

    @Override
    public void processPaymentRecordCreated(PaymentRecordPreparedEvent event) {
        long userId = event.getUserId();
        long orderId = event.getOrderId();
        long paymentId = event.getPaymentId();
        BigDecimal amountToPay = event.getAmountToPay();

        Order order = repository.findById(orderId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        if (order.getTotal().equals(amountToPay)) {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setPaymentStatus(PaymentStatus.PENDING);
            order.setPaymentId(paymentId);

            sagaManager.publishOrderStockAllocationRequestedEvent(order);
        } else {
            throw new IllegalStateException("Order total Amount has changed");
        }
    }

    @Transactional
    @Override
    public void processInventoryAllocationConfirmed(InventoryAllocationConfirmedEvent event) {

        long userId = event.getUserId();
        long orderId = event.getOrderId();
        Order order = repository.findWithItemsByIdAndUserIdForUpdate(orderId, userId)
                .orElseThrow(() -> new OrderEventNotFoundException(orderId, userId));

        Map<Long, AllocationItemSnapshot> mapItemByProductId = event
                .getAllocatedItems()
                .stream()
                .collect(Collectors.toMap(AllocationItemSnapshot::getProductId, Function.identity()));

        for (OrderItem oi : order.getOrderItems()) {
            long productId = oi.getProductId();
            AllocationItemSnapshot ais = mapItemByProductId.get(productId);
            if (ais != null) {
                oi.setAvgCostPrice(ais.getAvgCostPrice());
            }

        }

    }

}
