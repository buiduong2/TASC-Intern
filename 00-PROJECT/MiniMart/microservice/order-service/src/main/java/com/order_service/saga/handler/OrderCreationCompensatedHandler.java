package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.order_service.service.OrderService;
import com.order_service.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.SALES_ORDER_COMPENSATION, groupId = "order-creation-compensated-group")
public class OrderCreationCompensatedHandler {

    private final OrderService orderService;

    private final PaymentService paymentService;

    @KafkaHandler
    public void handleOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        log.warn("[SAGA][OrderId={}] Order creation compensated, status set to CREATION_FAILED", event.getOrderId());
        try {
            orderService.processOrderCreationCompensated(event);
        } catch (Exception e) {
            log.error("[SAGA][OrderService][OrderId={}][ACTION=CompensatePayment] ❌ Compensation failed: {}",
                    event.getOrderId(),
                    e.getMessage(),
                    e);
        }

        try {
            paymentService.processOrderCreationCompensated(event);
        } catch (Exception e) {
            log.error("[SAGA][PaymentService][OrderId={}][ACTION=CompensatePayment] ❌ Compensation failed: {}",
                    event.getOrderId(),
                    e.getMessage(),
                    e);
        }
    }

    @KafkaHandler(isDefault = true)
    public void handleOther(Object other, @Header(name = "__TypeId__", required = false) String typeId) {
        log.info("[KAFKA] Received message ingored , typeId={}", typeId);
    }
}
