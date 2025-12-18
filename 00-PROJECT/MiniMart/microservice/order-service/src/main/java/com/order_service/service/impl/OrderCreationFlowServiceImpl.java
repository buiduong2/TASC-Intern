package com.order_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.order_service.enums.SagaStepType;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.model.Order;
import com.order_service.service.OrderCoreService;
import com.order_service.service.OrderCreationFlowService;
import com.order_service.service.OrderSagaTrackerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderCreationFlowServiceImpl implements OrderCreationFlowService {

    private final OrderCoreService service;
    private final OrderSagaTrackerService trackerService;

    // 1. Begin Saga -> Validate Product
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleOrderPrepared(OrderPreparedDomainEvent event) {
        Order order = event.getOrder();
        long orderId = order.getId();

        trackerService.create(orderId);
        trackerService.startStep(orderId, SagaStepType.UNIT_PRICE_CONFIRMED);

    }

    // 2. Validate Product -> Reservation Inventory
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order processProductValidationPassedEvent(ProductValidationPassedEvent event) {
        final long orderId = event.getOrderId();

        Order order = service.processProductValidationPassedEvent(event);
        trackerService.completeStep(orderId, SagaStepType.UNIT_PRICE_CONFIRMED);
        trackerService.startStep(orderId, SagaStepType.STOCK_RESERVED);

        return order;
    }

    // 3. Reservation Inventory -> Initial PaymentRecord
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order processInventoryReservedConfirmed(InventoryReservedConfirmedEvent event) {
        final long orderId = event.getOrderId();

        Order order = service.processInventoryReservedConfirmed(event);

        trackerService.completeStep(orderId, SagaStepType.STOCK_RESERVED);
        trackerService.startStep(orderId, SagaStepType.PAYMENT_PROCESSED);

        return order;
    }

    // 3. Initial PaymentRecord -> InventoryStockAllocation
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order processPaymentRecordCreated(PaymentRecordPreparedEvent event) {
        final long orderId = event.getOrderId();

        Order order = service.processPaymentRecordCreated(event);

        trackerService.completeStep(orderId, SagaStepType.PAYMENT_PROCESSED);
        trackerService.startStep(orderId, SagaStepType.STOCK_FULFILLED);

        return order;
    }

    // 4.InventoryStockAllocation -> Complete
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order processInventoryAllocationConfirmed(InventoryAllocationConfirmedEvent event) {
        final long orderId = event.getOrderId();

        Order order = service.processInventoryAllocationConfirmed(event);
        trackerService.completeStep(orderId, SagaStepType.STOCK_FULFILLED);

        return order;
    }

    @Override
    public Order getOrderSummaryDTO(long orderId, long userId) {
        return service.getOrderSummaryDTOById(orderId, userId);
    }

}
