package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.order_service.enums.SagaStepType;
import com.order_service.model.Order;
import com.order_service.saga.OrderSagaManager;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.CATALOG_PRODUCT_EVENTS, groupId = "order-validation-group")
@Slf4j
public class ProductValidationHandler {

    private final OrderService orderService;
    private final OrderSagaTrackerService orderSagaTrackerService;
    private final OrderSagaManager orderSagaManager;

    @KafkaHandler
    public void handleValidationPassed(ProductValidationPassedEvent event) {
        log.info(
                "[SAGA][OrderId={}][STEP=UNIT_PRICE_CONFIRMED][EVENT=ProductValidationPassed] ✅ Product validated successfully",
                event.getOrderId());
        log.info("[SAGA][OrderId={}] Marked step SUCCESS, checking pre-payment readiness...", event.getOrderId());

        Order order = orderService.processProductValidationPassedEvent(event);
        orderSagaTrackerService.markSuccessStep(event.getOrderId(), SagaStepType.UNIT_PRICE_CONFIRMED);
        orderSagaManager.tryPublishOrderInitialPaymentEventOrCancel(order);

    }

    @KafkaHandler
    public void handleValidationFailed(ProductValidationFailedEvent event) {
        orderSagaTrackerService.markFailedStep(event.getOrderId(), SagaStepType.UNIT_PRICE_CONFIRMED,
                event.getReason());
        Order order = orderService.processProductValidationFailed(event);
        orderSagaManager.tryPublishCancellationEvent(order);

        log.warn(
                "[SAGA][OrderId={}][STEP=UNIT_PRICE_CONFIRMED][EVENT=ProductValidationFailed] ❌ Product validation failed: {}",
                event.getOrderId(), event.getReason());

    }

    @KafkaHandler(isDefault = true)
    public void handleOther(Object other, @Header(name = "__TypeId__", required = false) String typeId) {
        log.info("[KAFKA] Received message ingored , typeId={}", typeId);

    }
}
