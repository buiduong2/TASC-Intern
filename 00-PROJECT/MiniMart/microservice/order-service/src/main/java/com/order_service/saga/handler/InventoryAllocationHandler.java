package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.order_service.enums.SagaStepType;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_ALLOCATION, groupId = "order-inventory-success-group")
@Slf4j
public class InventoryAllocationHandler {

    private final OrderService orderService;

    private final OrderSagaTrackerService orderSagaTrackerService;

    @KafkaHandler
    public void handleAllocatioConfirmed(InventoryAllocationConfirmedEvent event) {
        orderService.processInventoryAllocationConfirmed(event);
        orderSagaTrackerService.markSuccessStep(event.getOrderId(), SagaStepType.STOCK_FULFILLED);

        log.info(
                "[SAGA][OrderId={}][STEP=STOCK_FULFILLED][EVENT=InventoryAllocationConfirmed] ✅ Stock allocation confirmed",
                event.getOrderId());
        log.info("[SAGA][OrderId={}] 🎯 Saga completed successfully", event.getOrderId());

    }

    @KafkaHandler(isDefault = true)
    public void handleOther(Object other, @Header(name = "__TypeId__", required = false) String typeId) {
        log.info("[KAFKA] Received message ingored , typeId={}", typeId);

    }
}
