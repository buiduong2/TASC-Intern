package com.order_service.saga.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.order_service.enums.SagaStepType;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.model.Order;
import com.order_service.saga.OrderSagaManager;
import com.order_service.service.OrderSagaTrackerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderPrepareHandler {

    private final OrderSagaTrackerService sagaTrackerService;

    private final OrderSagaManager sagaManager;

    /**
     * Entry point cho Order Saga.
     * Được gọi sau khi Order được tạo thành công (transaction commit).
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleOrderPrepared(OrderPreparedDomainEvent event) {
        Order order = event.getOrder();

        sagaTrackerService.create(order.getId());
        sagaTrackerService.startStep(order.getId(), SagaStepType.UNIT_PRICE_CONFIRMED);
        sagaTrackerService.startStep(order.getId(), SagaStepType.STOCK_RESERVED);
        sagaManager.publishOrderCreationRequestedEvent(order);

        log.info("[SAGA][OrderId={}] Start Saga. Steps initialized: UNIT_PRICE_CONFIRMED, STOCK_RESERVED",
                order.getId());
        log.info("[SAGA][OrderId={}] ▶️ Published OrderCreationRequestedEvent", order.getId());

    }

}
