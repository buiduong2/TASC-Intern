package com.inventory_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.inventory_service.model.Allocation;
import com.inventory_service.saga.AllocationSagaManager;
import com.inventory_service.saga.StockSagaManager;
import com.inventory_service.service.AllocationService;
import com.inventory_service.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.SALES_ORDER_CANCEL_COMMANDS, groupId = "inventory-cancelation-consumer-group")
public class OrderCancellationRequestedHandler {

    private final AllocationService allocationService;

    private final StockService stockService;

    private final StockSagaManager stockSagaManager;

    private final AllocationSagaManager allocationSagaManager;

    @KafkaHandler
    public void handleOrderCancellationRequested(OrderCancellationRequestedEvent event) {
        long orderId = event.getOrderId();
        log.info(
                "[SAGA][InventoryService][STEP=STOCK_COMPENSATED][OrderId={}] 🧾 Received OrderCancellationRequestedEvent",
                orderId);

        try {
            Allocation allocation = allocationService.processOrderCancellationRequested(event);
            allocationSagaManager.publishInventoryAllocationCompensateCompletedEvent(event, allocation);
        } catch (Exception e) {
            log.error("[SAGA][InventoryService][OrderId={}][ACTION=CompensateCommitedStock] ❌ Compensation failed: {}",
                    event.getOrderId(),
                    e.getMessage(),
                    e);
        }
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
