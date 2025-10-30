package com.order_service.saga.event;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedCompenstateCompletedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
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
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_RESERVATION_EVENTS, groupId = "order-inventory-reservation-group")
@Slf4j
public class InventoryReservationEventHandler {

    private final OrderService orderService;
    private final OrderSagaTrackerService orderSagaTrackerService;
    private final OrderSagaManager orderSagaManager;

    @KafkaHandler
    public void handleReservationConfirmed(InventoryReservedConfirmedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();

        log.info("🟢📥 [{}][InventoryReservedConfirmedEvent][RECEIVED][ID={}] Stock reserved successfully", SAGA,
                orderId);

        try {
            Order order = orderService.processInventoryReservedConfirmed(event);

            orderSagaTrackerService.completeStep(event.getOrderId(), SagaStepType.STOCK_RESERVED);
            orderSagaTrackerService.startStep(event.getOrderId(), SagaStepType.PAYMENT_PROCESSED);

            orderSagaManager.publishOrderInitialPaymentRequestedEvent(order);
            log.info("📤 [{}][OrderInitialPaymentRequestedEvent][PUBLISHED][ID={}] Request initial payment", SAGA,
                    orderId);
        } catch (ResourceNotFoundException e) {
            log.error("❌ [{}][{}][NOT_FOUND][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][INVALID_STATE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (Exception e) {
            log.error("❌ [{}][InventoryReservedConfirmedEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);

        }

    }

    @KafkaHandler
    public void handleReservationFailed(InventoryReservationFailedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        log.warn("🔴📥 [{}][InventoryReservationFailedEvent][RECEIVED][ID={}] Stock reservation failed", SAGA, orderId);

        try {
            orderSagaTrackerService.failedStep(orderId, SagaStepType.STOCK_RESERVED, event.getReason());
            orderService.processOrderCreationCompensated(event.getOrderId(), event.getUserId());

            log.warn("🧩📤 [{}][OrderCreationCompensatedEvent][PUBLISHED][ID={}] Trigger compensation flow", SAGA,
                    orderId);

        } catch (IdempotentEventException e) {
            // skip
        } catch (InvalidStateException e) {
            // skip
        } catch (Exception e) {
            log.error("❌ [{}][InventoryReservationFailedEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);
        }

    }

    @KafkaHandler
    public void handleReservationCompensated(InventoryReservedCompenstateCompletedEvent event) {
        orderSagaTrackerService.compensatedStep(event.getOrderId(), SagaStepType.STOCK_RESERVED);
    }

}
