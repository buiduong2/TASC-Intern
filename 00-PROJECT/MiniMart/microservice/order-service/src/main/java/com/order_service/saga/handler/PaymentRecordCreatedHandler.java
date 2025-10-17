package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.order_service.enums.SagaStepType;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.FINANCE_PAYMENT_EVENTS, groupId = "order-payment-record-group")
public class PaymentRecordCreatedHandler {
    private final OrderService orderService;
    private final OrderSagaTrackerService orderSagaTrackerService;

    @KafkaHandler
    public void handlePaymentRecordCreated(PaymentRecordPreparedEvent event) {
        orderService.processPaymentRecordCreated(event);
        orderSagaTrackerService.startStep(event.getOrderId(), SagaStepType.STOCK_FULFILLED);
    }
}
