package com.order_service.saga.domain;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.order_service.event.OrderCancelDomainEvent;
import com.order_service.event.OrderCompletedDomainEvent;
import com.order_service.event.OrderCreationFailedDomainEvent;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.model.Order;
import com.order_service.saga.handler.OrderCreationAsyncHandler;
import com.order_service.saga.handler.OrderCreationCompensationHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderDomainEventHandler {

    private final OrderCreationAsyncHandler creationAsyncHandler;

    private final OrderCreationCompensationHandler creationCompensationHandler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderPrepared(OrderPreparedDomainEvent event) {
        creationAsyncHandler.handleOrderPrepared(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreationFailed(OrderCreationFailedDomainEvent event) {
        creationCompensationHandler.handleOrderCreationFailed(event);

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCancel(OrderCancelDomainEvent event) {
        creationCompensationHandler.handleOrderCancel(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCompleted(OrderCompletedDomainEvent event) {
        Order order = event.getOrder();
        System.out.println(order);
    }
}