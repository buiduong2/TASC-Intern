package com.order_service.saga.event;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedCompenstateCompletedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.order_service.saga.handler.OrderCreationAsyncHandler;
import com.order_service.saga.handler.OrderCreationCompensationHandler;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_RESERVATION_EVENTS, groupId = "order-inventory-reservation-group")
public class InventoryReservationEventHandler {

    private final OrderCreationAsyncHandler creationHandler;

    private final OrderCreationCompensationHandler compensationHandler;

    @KafkaHandler
    public void handleReservationConfirmed(InventoryReservedConfirmedEvent event) {
        creationHandler.processInventoryReservedConfirmed(event);

    }

    @KafkaHandler
    public void handleReservationFailed(InventoryReservationFailedEvent event) {
        compensationHandler.processInventoryReservedFailed(event);

    }

    @KafkaHandler
    public void handleReservationCompensated(InventoryReservedCompenstateCompletedEvent event) {
        compensationHandler.handleReservationCompensated(event);
    }

}
