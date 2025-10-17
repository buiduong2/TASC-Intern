package com.order_service.saga.handler;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.order_service.enums.SagaStepType;
import com.order_service.model.Order;
import com.order_service.saga.OrderSagaManager;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.SUPPLY_INVENTORY_RESERVATION, groupId = "order-inventory-group")
@Slf4j
public class InventoryReservedHandler {

    private final OrderService orderService;
    private final OrderSagaTrackerService orderSagaTrackerService;
    private final OrderSagaManager orderSagaManager;

    @KafkaHandler
    public void handleReservationConfirmed(InventoryReservedConfirmedEvent event) {
        orderSagaTrackerService.markSuccessStep(event.getOrderId(), SagaStepType.STOCK_RESERVED);
        Order order = orderService.processInventoryReservedConfirmed(event);

        orderSagaManager.tryPublishOrderInitialPaymentEventOrCancel(order);

        log.info(
                "[SAGA][OrderId={}][STEP=STOCK_RESERVED][EVENT=InventoryReservedConfirmed] ✅ Stock reserved successfully",
                event.getOrderId());
        log.info("[SAGA][OrderId={}] Marked step SUCCESS, checking pre-payment readiness...", event.getOrderId());

    }

    @KafkaHandler
    public void handleReservationFailed(InventoryReservationFailedEvent event) {
        orderSagaTrackerService.markFailedStep(event.getOrderId(), SagaStepType.STOCK_RESERVED, event.getReason());
        Order order = orderService.processInventoryReservationFailed(event);

        orderSagaManager.tryPublishCancellationEvent(order);

        log.warn(
                "[SAGA][OrderId={}][STEP=STOCK_RESERVED][EVENT=InventoryReservationFailed] ❌ Stock reservation failed: {}",
                event.getOrderId(), event.getReason());

    }

    @KafkaHandler(isDefault = true)
    public void handleOther(Object other, @Header(name = "__TypeId__", required = false) String typeId) {
        log.info("[KAFKA] Received message ingored , typeId={}", typeId);

    }

}
