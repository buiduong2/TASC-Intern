package com.inventory_service.saga.command;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderStockReservationRequestedEvent;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.common_kafka.exception.saga.LogicViolationException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.inventory_service.dto.res.ReservateStockResult;
import com.inventory_service.model.Allocation;
import com.inventory_service.saga.StockSagaManager;
import com.inventory_service.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_RESERVATION, groupId = "inventory-reservation-comamnd-group")
public class InventoryReservationCommandHandler {

    private final StockService stockService;

    private final StockSagaManager stockSagaManager;

    @KafkaHandler
    public void handleOrderStockServationRequested(OrderStockReservationRequestedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";

        log.info("🟢📥 [{}][OrderStockServationRequested][RECEIVED][ID={}] Process stock reservation", SAGA, orderId);

        try {
            ReservateStockResult result = stockService.processOrderStockReservationRequested(event);

            stockSagaManager.publishInventoryReservedConfirmedEvent(
                    event,
                    result.getLogs(),
                    result.getAllocation());

            log.info("📤 [{}][InventoryReservedConfirmedEvent][PUBLISHED][ID={}] Stock reserved successfully", SAGA,
                    orderId);

        } catch (LogicViolationException e) {
            stockSagaManager.publishInventoryReservedFailedEvent(event, e.getMessage());
            log.warn("🔴📤 [{}][InventoryReservedFailedEvent][PUBLISHED][ID={}] Stock reservation failed: {}", SAGA,
                    orderId, e.getMessage());

        } catch (IdempotentEventException e) {
            log.warn("⚠️ [{}][OrderStockServationRequested][SKIPPED][IDEMPOTENT][ID={}] Already processed", SAGA,
                    orderId);

        } catch (Exception e) {
            stockSagaManager.publishInventoryReservedFailedEvent(event, "Unexpected error");
            log.error("❌ [{}][OrderStockServationRequested][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);
            log.warn("🔴📤 [{}][InventoryReservedFailedEvent][PUBLISHED][ID={}] Exception during reservation", SAGA,
                    orderId);
        }

    }

    @KafkaHandler
    public void handleOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        long orderId = event.getOrderId();
        log.info(
                "[SAGA][InventoryService][STEP=STOCK_COMPENSATED][OrderId={}] 🧾 Received OrderCreationCompensatedEvent",
                orderId);

        try {
            Allocation allocation = stockService.processOrderCreationCompensated(event);
            stockSagaManager.publishInventoryReservedCompenstateCompletedEvent(event, allocation);
        } catch (InvalidStateException e) {
            // skip
        } catch (ResourceNotFoundException e) {
            // skip
        } catch (Exception e) {
            log.error(
                    "[SAGA][InventoryService][OrderId={}][ACTION=CompensateReservationStock] ❌ Compensation failed: {}",
                    event.getOrderId(),
                    e.getMessage(),
                    e);

        }

    }

    @KafkaHandler
    public void handleOrderCancellationRequested(OrderCancellationRequestedEvent event) {
        long orderId = event.getOrderId();
        log.info(
                "[SAGA][InventoryService][STEP=STOCK_COMPENSATED][OrderId={}] 🧾 Received OrderCancellationRequestedEvent",
                orderId);

        try {
            Allocation allocation = stockService.processOrderCancellationRequested(event);
            stockSagaManager.publishInventoryReservedCompenstateCompletedEvent(event, allocation);
        } catch (Exception e) {
            log.error(
                    "[SAGA][InventoryService][OrderId={}][ACTION=CompensateReservationStock] ❌ Compensation failed: {}",
                    event.getOrderId(),
                    e.getMessage(),
                    e);

        }

    }

}
