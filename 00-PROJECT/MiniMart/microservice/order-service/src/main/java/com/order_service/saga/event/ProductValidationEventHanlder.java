package com.order_service.saga.event;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.order_service.enums.SagaStepType;
import com.order_service.model.Order;
import com.order_service.saga.OrderSagaManager;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.CATALOG_PRODUCT_VALIDATION_EVENTS, groupId = "order-validation-group")
@Slf4j
public class ProductValidationEventHanlder {

    private final OrderService orderService;
    private final OrderSagaTrackerService orderSagaTrackerService;
    private final OrderSagaManager orderSagaManager;

    @KafkaHandler
    public void handleValidationPassed(ProductValidationPassedEvent event) {

        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();

        log.info("🟢📥 [{}][ProductValidationPassedEvent][RECEIVED][ID={}] Product validation passed", SAGA, orderId);

        try {
            Order order = orderService.processProductValidationPassedEvent(event);
            orderSagaTrackerService.completeStep(event.getOrderId(), SagaStepType.UNIT_PRICE_CONFIRMED);
            orderSagaTrackerService.startStep(event.getOrderId(), SagaStepType.STOCK_RESERVED);
            orderSagaManager.publishOrderStockReservationRequestedEvent(order);
            log.info("📤 [{}][InventoryReserveRequestedEvent][PUBLISHED][ID={}] Request stock reservation", SAGA,
                    orderId);

        } catch (ResourceNotFoundException e) {
            log.error("❌ [{}][{}][NOT_FOUND][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][INVALID_STATE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (IdempotentEventException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][IDEMPOTENT][ID={}] Already processed, skip event",
                    SAGA, EVENT, orderId);
        } catch (Exception e) {
            log.error("🔥 [{}][{}][UNEXPECTED][ID={}] {}", SAGA, EVENT, orderId, e.getMessage(), e);
        }

    }

    @KafkaHandler
    public void handleValidationFailed(ProductValidationFailedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();

        log.warn("🔴📥 [{}][ProductValidationFailedEvent][RECEIVED][ID={}] Product validation failed", SAGA, orderId);
        try {
            orderSagaTrackerService.failedStep(event.getOrderId(), SagaStepType.UNIT_PRICE_CONFIRMED,
                    event.getReason());
            orderService.processOrderCreationCompensated(event.getUserId(), event.getOrderId());
            log.warn("🧩📤 [{}][OrderCreationCompensatedEvent][PUBLISHED][ID={}] Trigger compensation flow", SAGA,
                    orderId);
        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][INVALID_STATE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (Exception e) {
            log.error("🔥 [{}][{}][UNEXPECTED][ID={}] {}", SAGA, EVENT, orderId, e.getMessage(), e);
        }

    }
}
