package com.product_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCreationRequestedEvent;
import com.common_kafka.event.shared.res.SagaResult;
import com.product_service.dto.res.ProductValidationResult;
import com.product_service.saga.ProductSagaManager;
import com.product_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.SALES_ORDER_COMMAND, groupId = "catalog-validation-group")
@Slf4j
public class OrderCreationRequestedHandler {
    private final ProductService service;

    private final ProductSagaManager sagaManager;

    @KafkaHandler
    public void handleOrderCreationRequestedEvent(OrderCreationRequestedEvent event) {
        long orderId = event.getOrderId();
        log.info("[SAGA][Service=ProductService][OrderId={}] ⏳ Received OrderCreationRequestedEvent",
                orderId);

        SagaResult<ProductValidationResult> result = service.processOrderCreationRequested(event);

        if (result.getData().isAllValid()) {
            sagaManager.publishProductValidationPassedEvent(event, result.getData().getValidProducts());
            log.info("[SAGA][Service=ProductService][OrderId={}] 📤 Published ProductValidationPassedEvent",
                    orderId);

        } else {
            sagaManager.publishProductValidationFailedEvent(event, result.getData().getValidProducts());
            log.warn("[SAGA][Service=ProductService][OrderId={}] 📤 Published ProductValidationFailedEvent", orderId);

        }
    }
}
