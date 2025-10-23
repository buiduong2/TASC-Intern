package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.finance.payment.PaymentSucceededEvent;
import com.order_service.enums.SagaStepType;
import com.order_service.model.Order;
import com.order_service.saga.OrderSagaManager;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.FINANCE_PAYMENT_EVENTS, groupId = "order-payment-record-group")
@Slf4j
public class PaymentRecordCreatedHandler {
    private final OrderService orderService;
    private final OrderSagaTrackerService orderSagaTrackerService;
    private final OrderSagaManager orderSagaManager;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @KafkaHandler
    public void handlePaymentRecordCreated(PaymentRecordPreparedEvent event) {
        Order order = orderService.processPaymentRecordCreated(event);

        orderSagaTrackerService.startStep(event.getOrderId(), SagaStepType.STOCK_FULFILLED);
        orderSagaManager.publishOrderStockAllocationRequestedEvent(order);

        log.info("[SAGA][OrderId={}][STEP=PAYMENT_PROCESSED][EVENT=PaymentRecordPrepared] ✅ Payment record prepared",
                event.getOrderId());
        log.info("[SAGA][OrderId={}] ▶️ Published OrderStockAllocationRequestedEvent", event.getOrderId());
    }

    @KafkaHandler
    public void handlePaymentSucceededEvent(PaymentSucceededEvent event) {
        orderSagaTrackerService.markSuccessStep(event.getOrderId(), SagaStepType.PAYMENT_PROCESSED);
        orderService.processPaymentSucceedEvent(event);
    }

    @KafkaHandler(isDefault = true)
    public void handleOther(Object other, @Header(name = "__TypeId__", required = false) String typeId) {
        log.info("[KAFKA] Received message ingored , typeId={}", typeId);

    }
}
