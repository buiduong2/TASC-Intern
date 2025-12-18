package com.order_service.saga.event;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.order_service.saga.handler.OrderCreationAsyncHandler;
import com.order_service.saga.handler.OrderCreationCompensationHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.CATALOG_PRODUCT_VALIDATION_EVENTS, groupId = "order-validation-group")
public class ProductValidationEventHanlder {

    private final OrderCreationAsyncHandler creationHandler;

    private final OrderCreationCompensationHandler compensationHandler;

    @KafkaHandler
    public void handleValidationPassed(ProductValidationPassedEvent event) {
        creationHandler.processProductValidationPassedEvent(event);
    }

    @KafkaHandler
    public void handleValidationFailed(ProductValidationFailedEvent event) {
        compensationHandler.processProductValidationFailedEvent(event);
    }
}
