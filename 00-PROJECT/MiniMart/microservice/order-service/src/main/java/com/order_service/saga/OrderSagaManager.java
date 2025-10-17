package com.order_service.saga;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCreationRequestedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.sales.order.OrderStockAllocationRequestedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.order_service.model.Order;
import com.order_service.service.OrderSagaTrackerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final OrderSagaTrackerService trackerService;

    public void publishOrderCreationRequestedEvent(Order order) {
        kafkaTemplate.send(
                KafkaTopics.SALES_ORDER_COMMAND,
                order.getId().toString(),
                new OrderCreationRequestedEvent(
                        order.getId(),
                        order.getUserId(),
                        order.getOrderItems()
                                .stream()
                                .map(oi -> new OrderItemData(oi.getId(), oi.getProductId(), oi.getQuantity()))
                                .collect(Collectors.toCollection(LinkedHashSet::new))));
    }

    public void tryPublishOrderInitialPaymentEventOrCancel(Order order) {
        checkAndAdvanceOrder(order);
    }

    public void tryPublishCancellationEvent(Order order) {

        checkAndAdvanceOrder(order);
    }

    public void checkAndAdvanceOrder(Order order) {
        Boolean isReady = trackerService.checkPrePaymentReadinessOrCancelReadiness(order.getId(), order.getUserId());
        if (Boolean.TRUE.equals(isReady)) {
            publishOrderInitialPaymentRequestedEvent(order);
        } else if (Boolean.FALSE.equals(isReady)) {
            publishCancellationEvent(order);
        }

    }

    private void publishOrderInitialPaymentRequestedEvent(Order order) {
        log.info("[SAGA][OrderId={}][ACTION=publishPaymentRequested] ✅ Published OrderInitialPaymentRequestedEvent",
                order.getId());

        OrderInitialPaymentRequestedEvent event = new OrderInitialPaymentRequestedEvent(
                order.getId(),
                order.getUserId(),
                order.getTotal(),
                order.getPaymentMethod().name());

        kafkaTemplate.send(
                KafkaTopics.SALES_ORDER_COMMAND,
                String.valueOf(event.getOrderId()),
                event);
    }

    private void publishCancellationEvent(Order order) {
        log.warn("[SAGA][OrderId={}][ACTION=publishCancellation] ❌ Saga canceled", order.getId());

        // ... logic tạo và gửi OrderCanceledEvent
    }

    public void publishOrderStockAllocationRequestedEvent(Order order) {

        OrderStockAllocationRequestedEvent event = new OrderStockAllocationRequestedEvent(
                order.getId(),
                order.getUserId(),
                order.getOrderItems()
                        .stream()
                        .map(oi -> new OrderItemData(oi.getId(), oi.getProductId(), oi.getQuantity()))
                        .collect(Collectors.toCollection(LinkedHashSet::new)));

        kafkaTemplate.send(
                KafkaTopics.SALES_ORDER_COMMAND,
                String.valueOf(event.getOrderId()),
                event);
    }

}
