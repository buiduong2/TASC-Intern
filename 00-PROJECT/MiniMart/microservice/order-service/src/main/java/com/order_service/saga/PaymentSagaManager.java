package com.order_service.saga;

import java.util.Optional;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.finance.payment.PaymentCompensationCompletedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.finance.payment.PaymentRefundedEvent;
import com.common_kafka.event.finance.payment.PaymentPaidEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.shared.AbstractSagaEvent;
import com.order_service.model.Payment;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentRecordPreparedEvent(OrderInitialPaymentRequestedEvent event, Payment payment) {

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

    public void publishPaymentCompensationCompletedEvent(AbstractSagaEvent event, Optional<Payment> payment) {

        PaymentCompensationCompletedEvent resultEvent = new PaymentCompensationCompletedEvent(
                event.getOrderId(),
                event.getUserId(),
                payment.map(p -> p.getId()).orElse(null),
                payment.map(p -> p.getStatus().name()).orElse(null),
                payment.map(p -> p.getAmountTotal()).orElse(null));

        kafkaTemplate.send(
                KafkaTopics.FINANCE_PAYMENT_EVENTS,
                String.valueOf(event.getOrderId()),
                resultEvent);
    }

    public void publishPaymentPaidEvent(Payment payment) {
        PaymentPaidEvent event = new PaymentPaidEvent(
                payment.getOrderId(),
                payment.getUserId(),
                payment.getId());

        kafkaTemplate.send(
                KafkaTopics.FINANCE_PAYMENT_EVENTS,
                String.valueOf(payment.getOrderId()),
                event);
    }

    public void publishPaymentRefunedEvent(Payment payment) {
        PaymentRefundedEvent event = new PaymentRefundedEvent(
                payment.getOrderId(),
                payment.getUserId(),
                payment.getId());

        kafkaTemplate.send(
                KafkaTopics.FINANCE_PAYMENT_EVENTS,
                String.valueOf(payment.getOrderId()),
                event);
    }
}
