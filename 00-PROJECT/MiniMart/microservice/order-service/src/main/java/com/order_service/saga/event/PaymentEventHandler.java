package com.order_service.saga.event;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.finance.payment.PaymentCompensationCompletedEvent;
import com.common_kafka.event.finance.payment.PaymentPaidEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.finance.payment.PaymentRefundedEvent;
import com.order_service.saga.handler.OrderCreationAsyncHandler;
import com.order_service.saga.handler.OrderCreationCompensationHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.FINANCE_PAYMENT_EVENTS, groupId = "order-payment-record-group")
public class PaymentEventHandler {

    private final OrderCreationAsyncHandler creationHandler;

    private final OrderCreationCompensationHandler creationCompensationHandler;

    @KafkaHandler
    public void handlePaymentRecordCreated(PaymentRecordPreparedEvent event) {
        creationHandler.processPaymentRecordCreated(event);
    }

    @KafkaHandler
    public void handlePaymentCompensationCompletedEvent(PaymentCompensationCompletedEvent event) {
        creationCompensationHandler.handlePaymentCompensationCompletedEvent(event);
    }

    @KafkaHandler
    public void handlePaymentPaidEvent(PaymentPaidEvent event) {
        // creationHandler.processPaymentPaidEvent(event);
    }

    @KafkaHandler
    public void handlePaymentRefunded(PaymentRefundedEvent event) {
        creationCompensationHandler.processPaymentRefunded(event);
    }

}
