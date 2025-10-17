package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.order_service.enums.SagaStepType;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.CATALOG_PRODUCT_EVENTS, groupId = "order-validation-group")
public class ProductValidationHandler {

    private final OrderService orderService;
    private final OrderSagaTrackerService orderSagaTrackerService;

    @KafkaHandler
    public void handleValidationPassed(ProductValidationPassedEvent event) {
        orderSagaTrackerService.markSuccessStep(event.getOrderId(), SagaStepType.UNIT_PRICE_CONFIRMED);
        orderService.processProductValidationPassedEvent(event);
    }

    @KafkaHandler
    public void handleValidationFailed(ProductValidationFailedEvent event) {
        orderSagaTrackerService.markFailedStep(
                event.getOrderId(),
                SagaStepType.UNIT_PRICE_CONFIRMED,
                event.getReason());
        orderService.processProductValidationFailed(event);
    }
}
