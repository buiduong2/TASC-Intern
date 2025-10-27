package com.inventory_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderStockAllocationRequestedEvent;
import com.common_kafka.event.shared.res.SagaResult;
import com.inventory_service.dto.res.StockAllocationResult;
import com.inventory_service.saga.AllocationSagaManager;
import com.inventory_service.service.AllocationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_ALLOCATION, groupId = "inventory-allocation-group")
public class OrderStockAllocationRequestedHandler {

    private final AllocationService allocationService;

    private final AllocationSagaManager allocationSagaManager;

    @KafkaHandler
    public void handleStockAllocationRequested(OrderStockAllocationRequestedEvent event) {
        long orderId = event.getOrderId();
        log.info("[SAGA][InventoryService][OrderId={}] ⏳ Received OrderStockAllocationRequestedEvent", orderId);

        SagaResult<StockAllocationResult> result = allocationService.processOrderStockAllocationRequest(event);

        if (result.isSuccess()) {
            allocationSagaManager.publishInventoryAllocationConfirmedEvent(event, result.getData());
            log.info("[SAGA][InventoryService][OrderId={}] 📤 Published InventoryAllocationConfirmedEvent", orderId);
        } else {
            log.warn("[SAGA][InventoryService][OrderId={}] ❌ Allocation failed: {}", orderId, result.getReason());
        }
    }

    @KafkaHandler(isDefault = true)
    public void handleOther(Object other, @Header(name = "__TypeId__", required = false) String typeId) {
        log.info("[KAFKA] Received message ingored , typeId={}", typeId);

    }
}
