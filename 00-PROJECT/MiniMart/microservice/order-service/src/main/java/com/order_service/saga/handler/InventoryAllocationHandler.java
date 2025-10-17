package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.order_service.enums.SagaStepType;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_ALLOCATION, groupId = "order-inventory-success-group")
public class InventoryAllocationHandler {

    private final OrderService orderService;

    private final OrderSagaTrackerService orderSagaTrackerService;

    @KafkaHandler
    public void handleAllocatioConfirmed(InventoryAllocationConfirmedEvent event) {
        orderSagaTrackerService.markSuccessStep(event.getOrderId(), SagaStepType.STOCK_FULFILLED);
        orderService.processInventoryAllocationConfirmed(event);
    }

}
