package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
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
@KafkaListener(topics = KafkaTopics.SALES_ORDER_COMMAND, groupId = "payment-initial-group")
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
}