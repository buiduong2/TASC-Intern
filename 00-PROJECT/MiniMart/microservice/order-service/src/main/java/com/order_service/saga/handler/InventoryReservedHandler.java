package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.order_service.enums.SagaStepType;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_RESERVATION, groupId = "order-inventory-group")
public class InventoryReservedHandler {
    private final OrderService orderService;
    private final OrderSagaTrackerService orderSagaTrackerService;

    @KafkaHandler
    public void handleReservationConfirmed(InventoryReservedConfirmedEvent event) {
        orderSagaTrackerService.markSuccessStep(event.getOrderId(), SagaStepType.STOCK_RESERVED);
        orderService.processInventoryReservedConfirmed(event);

    }

    @KafkaHandler
    public void handleReservationFailed(InventoryReservationFailedEvent event) {
        orderSagaTrackerService.markFailedStep(event.getOrderId(), SagaStepType.STOCK_RESERVED, event.getReason());
        orderService.processInventoryReservationFailed(event);
    }

}
