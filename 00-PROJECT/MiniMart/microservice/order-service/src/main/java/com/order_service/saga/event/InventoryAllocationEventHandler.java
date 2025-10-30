package com.order_service.saga.event;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.supply.inventory.InventoryAllocationCompensateCompletedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.order_service.enums.SagaStepType;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_ALLOCATION_EVENTS, groupId = "order-inventory-allocation-group")
@Slf4j
public class InventoryAllocationEventHandler {

    private final OrderService orderService;

    private final OrderSagaTrackerService orderSagaTrackerService;

    @KafkaHandler
    public void handleAllocatioConfirmed(InventoryAllocationConfirmedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();

        log.info("🏁📥 [{}][InventoryAllocationConfirmedEvent][RECEIVED][ID={}] Stock allocated, saga complete", SAGA,
                orderId);

        try {
            orderService.processInventoryAllocationConfirmed(event);
            orderSagaTrackerService.completeStep(orderId, SagaStepType.STOCK_FULFILLED);
            log.info("🏁📤 [{}][OrderCreatedEvent][PUBLISHED][ID={}] Saga completed successfully ✅", SAGA, orderId);

        } catch (ResourceNotFoundException e) {
            log.error("❌ [{}][{}][NOT_FOUND][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][INVALID_STATE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (IdempotentEventException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][IDEMPOTENT][ID={}] Already processed, skip event",
                    SAGA, EVENT, orderId);
        } catch (Exception e) {
            log.error("❌ [{}][InventoryAllocationConfirmedEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);
        }
    }

    @KafkaHandler
    public void handleInventoryAllocationCompensateCompletedEvent(InventoryAllocationCompensateCompletedEvent event) {
        orderSagaTrackerService.compensatedStep(event.getOrderId(), SagaStepType.STOCK_FULFILLED);
        orderSagaTrackerService.compensatedStep(event.getOrderId(), SagaStepType.STOCK_RESERVED);

    }

}
