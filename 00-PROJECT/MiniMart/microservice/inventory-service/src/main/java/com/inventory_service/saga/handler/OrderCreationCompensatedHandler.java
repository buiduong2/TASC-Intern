package com.inventory_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.inventory_service.service.AllocationService;
import com.inventory_service.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.GLOBAL_COMPENSATION_EVENTS, groupId = "inventory-creation-compensated-group")
public class OrderCreationCompensatedHandler {

    private final AllocationService allocationService;

    private final StockService stockService;

    @KafkaHandler
    public void handleOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        long orderId = event.getOrderId();
        log.info(
                "[SAGA][InventoryService][STEP=STOCK_COMPENSATED][OrderId={}] 🧾 Received OrderCreationCompensatedEvent",
                orderId);

        try {
            allocationService.processOrderCreationCompensated(event);
        } catch (Exception e) {
            log.error("[SAGA][InventoryService][OrderId={}][ACTION=CompensateCommitedStock] ❌ Compensation failed: {}",
                    event.getOrderId(),
                    e.getMessage(),
                    e);
        }
        try {
            stockService.processOrderCreationCompensated(event);
        } catch (Exception e) {
            log.error(
                    "[SAGA][InventoryService][OrderId={}][ACTION=CompensateReservationStock] ❌ Compensation failed: {}",
                    event.getOrderId(),
                    e.getMessage(),
                    e);

        }

    }

    @KafkaHandler(isDefault = true)
    public void handleOther(Object other, @Header(name = "__TypeId__", required = false) String typeId) {
        log.info("[KAFKA] Received message ingored , typeId={}", typeId);

    }
}
