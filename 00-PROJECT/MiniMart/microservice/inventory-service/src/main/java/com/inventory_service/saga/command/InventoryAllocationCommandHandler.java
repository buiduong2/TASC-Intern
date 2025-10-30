package com.inventory_service.saga.command;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderStockAllocationRequestedEvent;
import com.common_kafka.exception.saga.BusinessInvariantViolationException;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.inventory_service.dto.res.StockAllocationResult;
import com.inventory_service.model.Allocation;
import com.inventory_service.saga.AllocationSagaManager;
import com.inventory_service.service.AllocationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_ALLOCATION, groupId = "inventory-allocation-comamnd-group")
public class InventoryAllocationCommandHandler {
    private final AllocationService allocationService;

    private final AllocationSagaManager allocationSagaManager;

    @KafkaHandler
    public void handleStockAllocationRequested(OrderStockAllocationRequestedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";

        log.info("🟢📥 [{}][OrderStockAllocationRequestedEvent][RECEIVED][ID={}] Allocate stock for order", SAGA,
                orderId);

        try {
            StockAllocationResult result = allocationService.processOrderStockAllocationRequest(event);
            allocationSagaManager.publishInventoryAllocationConfirmedEvent(event, result);
            log.info("📤 [{}][InventoryAllocationConfirmedEvent][PUBLISHED][ID={}] Stock allocation confirmed", SAGA,
                    orderId);

        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][INVALID_STATE][ID={}] {}", SAGA, "OrderStockAllocationRequestedEvent", orderId,
                    e.getMessage());

        } catch (BusinessInvariantViolationException e) {
            log.warn("🔴 [{}][{}][BUSINESS_INVARIANT][ID={}] {}", SAGA, "OrderStockAllocationRequestedEvent", orderId,
                    e.getMessage());

        } catch (Exception e) {
            log.error("🔥 [{}][{}][UNEXPECTED][ID={}] {}", SAGA, "OrderStockAllocationRequestedEvent", orderId,
                    e.getMessage(), e);

        }
    }

    @KafkaHandler
    public void handleOrderCancellationRequested(OrderCancellationRequestedEvent event) {
        long orderId = event.getOrderId();
        log.info(
                "[SAGA][InventoryService][STEP=STOCK_COMPENSATED][OrderId={}] 🧾 Received OrderCancellationRequestedEvent",
                orderId);

        try {
            Allocation allocation = allocationService.processOrderCancellationRequested(event);
            allocationSagaManager.publishInventoryAllocationCompensateCompletedEvent(event, allocation);
        } catch (IdempotentEventException e) {
            log.info("[InventoryService] Duplicate cancel for orderId={}, skip", orderId);
        } catch (Exception e) {
            log.error("[SAGA][InventoryService][OrderId={}][ACTION=CompensateCommitedStock] ❌ Compensation failed: {}",
                    event.getOrderId(),
                    e.getMessage(),
                    e);
        }

    }

}
