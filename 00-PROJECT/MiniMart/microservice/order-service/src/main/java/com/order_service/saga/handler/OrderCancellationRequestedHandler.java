package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.order_service.model.Payment;
import com.order_service.saga.PaymentSagaManager;
import com.order_service.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.SALES_ORDER_CANCEL_COMMANDS, groupId = "payment-cancelation-consumer-group")
public class OrderCancellationRequestedHandler {

    private final PaymentService paymentService;

    private final PaymentSagaManager paymentSagaManager;

    @KafkaHandler
    public void handleOrderCancellationRequested(OrderCancellationRequestedEvent event) {
        Payment payment = paymentService.processOrderCancellationRequested(event);
        paymentSagaManager.publishPaymentCompensationCompletedEvent(payment);

    }

    @KafkaHandler(isDefault = true)
    public void handleOther(Object other, @Header(name = "__TypeId__", required = false) String typeId) {
        log.info("[KAFKA] Received message ingored , typeId={}", typeId);
    }

}
