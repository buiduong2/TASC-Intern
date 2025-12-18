package com.order_service.saga.handler.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.finance.payment.PaymentCompensationCompletedEvent;
import com.common_kafka.event.finance.payment.PaymentRefundedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationCompensateCompletedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedCompenstateCompletedEvent;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.order_service.enums.SagaStepType;
import com.order_service.event.OrderCancelDomainEvent;
import com.order_service.event.OrderCreationFailedDomainEvent;
import com.order_service.model.Order;
import com.order_service.saga.OrderSagaManager;
import com.order_service.saga.handler.OrderCreationCompensationHandler;
import com.order_service.service.OrderCoreService;
import com.order_service.service.OrderSagaTrackerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderCreationCompensationHandlerImpl implements OrderCreationCompensationHandler {

    private final OrderSagaManager sagaManager;

    private final OrderCoreService service;

    private final OrderSagaTrackerService trackerService;

    private final ApplicationEventPublisher eventPublisher;

    private final String SAGA = "ORDER_CREATE";

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order processProductValidationFailedEvent(ProductValidationFailedEvent event) {
        final long orderId = event.getOrderId();
        final String EVENT = event.getClass().getSimpleName();

        log.warn("🔴📥 [{}][ProductValidationFailedEvent][RECEIVED][ID={}] Product validation failed", SAGA, orderId);
        try {
            trackerService.failedStep(
                    event.getOrderId(),
                    SagaStepType.UNIT_PRICE_CONFIRMED,
                    event.getReason());
            Order order = service.processOrderCreationCompensated(event.getUserId(), event.getOrderId());
            eventPublisher.publishEvent(new OrderCreationFailedDomainEvent(order));
            log.warn("🧩📤 [{}][OrderCreationCompensatedEvent][PUBLISHED][ID={}] Trigger compensation flow", SAGA,
                    orderId);
        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][INVALID_STATE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (Exception e) {
            log.error("🔥 [{}][{}][UNEXPECTED][ID={}] {}", SAGA, EVENT, orderId, e.getMessage(), e);
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order processInventoryReservedFailed(InventoryReservationFailedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        log.warn("🔴📥 [{}][InventoryReservationFailedEvent][RECEIVED][ID={}] Stock reservation failed", SAGA, orderId);

        try {
            trackerService.failedStep(orderId, SagaStepType.STOCK_RESERVED, event.getReason());
            Order order = service.processOrderCreationCompensated(orderId, event.getUserId());
            eventPublisher.publishEvent(new OrderCreationFailedDomainEvent(order));

            log.warn("🧩📤 [{}][OrderCreationCompensatedEvent][PUBLISHED][ID={}] Trigger compensation flow", SAGA,
                    orderId);

            return order;

        } catch (IdempotentEventException e) {
            // skip
        } catch (InvalidStateException e) {
            // skip
        } catch (Exception e) {
            log.error("❌ [{}][InventoryReservationFailedEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);
        }
        return null;

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Order processPaymentRefunded(PaymentRefundedEvent event) {

        final long orderId = event.getOrderId();
        final String SAGA = "Payment_PAID";
        final String EVENT = event.getClass().getSimpleName();

        try {
            service.processPaymentRefunded(event);
        } catch (IdempotentEventException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][IDEMPOTENT][ID={}] Already processed, skip event",
                    SAGA, EVENT, orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleReservationCompensated(InventoryReservedCompenstateCompletedEvent event) {
        trackerService.compensatedStep(event.getOrderId(), SagaStepType.STOCK_RESERVED);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleInventoryAllocationCompensateCompletedEvent(InventoryAllocationCompensateCompletedEvent event) {
        trackerService.compensatedStep(event.getOrderId(), SagaStepType.STOCK_FULFILLED);
        trackerService.compensatedStep(event.getOrderId(), SagaStepType.STOCK_RESERVED);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handlePaymentCompensationCompletedEvent(PaymentCompensationCompletedEvent event) {
        trackerService.compensatedStep(event.getOrderId(), SagaStepType.PAYMENT_PROCESSED);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleOrderCreationFailed(OrderCreationFailedDomainEvent event) {
        sagaManager.publishCreationCompensatedEvent(event.getOrder());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void handleOrderCancel(OrderCancelDomainEvent event) {
        sagaManager.publishOrderCancelRequestedEvent(event.getOrder());
    }

}
