package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.shared.res.SagaResult;
import com.order_service.model.Payment;
import com.order_service.saga.PaymentSagaManager;
import com.order_service.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.FINANCE_PAYMENT_REQUEST, groupId = "payment-initial-group")
public class OrderInitialPaymentRequestedHandler {

    private final PaymentService paymentService;
    private final PaymentSagaManager sagaManager;

    @KafkaHandler
    public void handleInitialPaymentRequest(OrderInitialPaymentRequestedEvent event) {
        long orderId = event.getOrderId();
        log.info("[SAGA][Service=PaymentService][OrderId={}] ⏳ Received OrderInitialPaymentRequestedEvent", orderId);

        SagaResult<Payment> result = paymentService.processInitialPaymentRequest(event);
        if (result.isSuccess()) {
            sagaManager.publishPaymentRecordPreparedEvent(event, result);
            log.info("[SAGA][Service=PaymentService][OrderId={}] 📤 Published PaymentRecordPreparedEvent", orderId);
        }

    }

    @KafkaHandler(isDefault = true)
    public void handleOther(Object other, @Header(name = "__TypeId__", required = false) String typeId) {
        log.info("[KAFKA] Received message ingored , typeId={}", typeId);

    }

}