package com.inventory_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.shared.res.SagaResult;
import com.inventory_service.dto.res.ReservateStockResult;
import com.inventory_service.saga.StockSagaManager;
import com.inventory_service.service.StockService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.CATALOG_PRODUCT_EVENTS, groupId = "inventory-validation-group")
public class ProductValidationHandler {

    private final StockService stockService;

    private final StockSagaManager stockSagaManager;

    @KafkaHandler
    public void handleValidationPassedEvent(ProductValidationPassedEvent event) {
        long orderId = event.getOrderId();
        log.info("[SAGA][Service=InventoryService][OrderId={}] ⏳ Received ProductValidationPassedEvent", orderId);

        SagaResult<ReservateStockResult> result = stockService.processProductValidationEvent(event);

        if (result.isSuccess()) {
            stockSagaManager.publishInventoryReservedConfirmedEvent(
                    event,
                    result.getData().getLogs(),
                    result.getData().getAllocation());
            log.info("[SAGA][Service=InventoryService][OrderId={}] 📤 Published InventoryReservedConfirmedEvent",
                    orderId);

        } else {
            stockSagaManager.publishInventoryReservedFailedEvent(
                    event,
                    result.getReason());
            log.warn("[SAGA][Service=InventoryService][OrderId={}] ❌ Stock reservation failed: {}", orderId,
                    result.getReason());
        }

    }
}
