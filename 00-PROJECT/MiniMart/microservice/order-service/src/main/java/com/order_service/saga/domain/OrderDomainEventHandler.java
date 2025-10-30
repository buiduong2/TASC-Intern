package com.order_service.saga.domain;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.order_service.enums.SagaStepType;
import com.order_service.event.OrderCancelDomainEvent;
import com.order_service.event.OrderCompletedDomainEvent;
import com.order_service.event.OrderCreationFailedDomainEvent;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.model.Order;
import com.order_service.saga.OrderSagaManager;
import com.order_service.service.OrderSagaTrackerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDomainEventHandler {

    private final OrderSagaTrackerService sagaTrackerService;

    private final OrderSagaManager sagaManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPrepared(OrderPreparedDomainEvent event) {
        Order order = event.getOrder();
        long orderId = order.getId();
        String SAGA = "ORDER_CREATE";

        log.info("🟢📥 [{}][OrderPreparedDomainEvent][RECEIVED][ID={}] Saga started", SAGA, orderId);

        try {
            sagaTrackerService.create(order.getId());
            sagaTrackerService.startStep(order.getId(), SagaStepType.UNIT_PRICE_CONFIRMED);
            sagaManager.publishOrderProductValidationRequestedEvent(order);
            log.info("📤 [{}][ProductValidationRequestedEvent][PUBLISHED][ID={}] Request product validation", SAGA,
                    orderId);

        } catch (Exception e) {
            log.error("❌ [{}][OrderPreparedDomainEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);

        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreationFailed(OrderCreationFailedDomainEvent event) {
        Order order = event.getOrder();
        sagaManager.publishCreationCompensatedEvent(order);
        // sagaManager.publishedOrderCreationFailed(order)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancel(OrderCancelDomainEvent event) {
        Order order = event.getOrder();
        sagaManager.publishOrderCancelRequestedEvent(order);
        // sagaManager.publishOrderCancledEvent(order)
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedDomainEvent event) {
        Order order = event.getOrder();
        System.out.println(order);
        // sagaManager.publishOrderCompletedEvent(order);
    }
}