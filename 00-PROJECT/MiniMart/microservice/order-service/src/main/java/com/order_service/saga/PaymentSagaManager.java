package com.order_service.saga;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.finance.payment.PaymentCompensationCompletedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.finance.payment.PaymentSucceededEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.shared.res.SagaResult;
import com.order_service.model.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentRecordPreparedEvent(OrderInitialPaymentRequestedEvent event, SagaResult<Payment> result) {
        Payment payment = result.getData();
        PaymentRecordPreparedEvent preparedEvent = new PaymentRecordPreparedEvent(
                payment.getOrderId(),
                payment.getUserId(),
                payment.getId(),
                payment.getAmountTotal());

        kafkaTemplate.send(
                KafkaTopics.FINANCE_PAYMENT_EVENTS,
                String.valueOf(payment.getOrderId()),
                preparedEvent);
    }

    public void publishPaymentSucceededEvent(Payment payment) {
        PaymentSucceededEvent event = new PaymentSucceededEvent(
                payment.getOrderId(),
                payment.getUserId(),
                payment.getId());

        kafkaTemplate.send(
                KafkaTopics.FINANCE_PAYMENT_EVENTS,
                String.valueOf(payment.getOrderId()),
                event);
    }

    public void publishPaymentCompensationCompletedEvent(Payment payment) {

        PaymentCompensationCompletedEvent event = new PaymentCompensationCompletedEvent(
                payment.getOrderId(),
                payment.getUserId(),
                payment.getId(),
                payment.getStatus().name(),
                payment.getAmountPaid());

        kafkaTemplate.send(
                KafkaTopics.FINANCE_PAYMENT_EVENTS,
                String.valueOf(payment.getOrderId()),
                event);
    }
}
