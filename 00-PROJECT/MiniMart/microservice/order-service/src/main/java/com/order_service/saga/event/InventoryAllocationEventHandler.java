package com.order_service.saga.event;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.supply.inventory.InventoryAllocationCompensateCompletedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.order_service.saga.handler.OrderCreationAsyncHandler;
import com.order_service.saga.handler.OrderCreationCompensationHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_ALLOCATION_EVENTS, groupId = "order-inventory-allocation-group")
public class InventoryAllocationEventHandler {

    private final OrderCreationAsyncHandler creationAsyncHandler;

    private final OrderCreationCompensationHandler creationCompensationHandler;

    @KafkaHandler
    public void handleAllocatioConfirmed(InventoryAllocationConfirmedEvent event) {
        creationAsyncHandler.processInventoryAllocationConfirmed(event);

    }

    @KafkaHandler
    public void handleInventoryAllocationCompensateCompletedEvent(InventoryAllocationCompensateCompletedEvent event) {
        creationCompensationHandler.handleInventoryAllocationCompensateCompletedEvent(event);
    }

}
