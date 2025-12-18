package com.order_service.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentStatus;
import com.order_service.mapper.OrderMapper;
import com.order_service.model.Order;
import com.order_service.model.OrderItem;
import com.order_service.repository.OrderRepository;
import com.order_service.repository.ShippingMethodRepository;
import com.order_service.service.OrderCoreService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderCoreServiceImpl implements OrderCoreService {

    private final OrderRepository repository;

    private final ShippingMethodRepository shippingMethodRepository;

    private final OrderMapper mapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order createRaw(OrderCreateReq req, long userId) {
        Order order = mapper.toEntity(req);
        order.setStatus(OrderStatus.VALIDATING);
        order.setPaymentStatus(PaymentStatus.PREPARING);
        order.setUserId(userId);
        order.setShippingMethod(shippingMethodRepository.findById(req.getShippingMethodId()).get());
        repository.save(order);
        return order;
    }

    /**
     * Tiến hành cập nhật Unit Price + total Price sẵn sàng cho tạo PaymentRecord
     */

    @Transactional(propagation = Propagation.REQUIRED)
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
            } else {
                throw new RuntimeException("Some thing wrong");
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

    @Override
    public Order getOrderSummaryDTOById(long orderId, long userId) {
        return repository.findByIdAndUserIdForCLientSummary(orderId, userId)
                .orElseThrow();
    }

}
