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
import com.order_service.enums.SagaStepType;
import com.order_service.model.Order;
import com.order_service.service.OrderSagaTrackerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final OrderSagaTrackerService sagaTrackerService;

    public void publishOrderCreationRequestedEvent(Order order) {
        sagaTrackerService.startStep(order.getId(), SagaStepType.UNIT_PRICE_CONFIRMED);
        sagaTrackerService.startStep(order.getId(), SagaStepType.STOCK_RESERVED);

        kafkaTemplate.send(
                KafkaTopics.SALES_ORDER_COMMAND,
                order.getId().toString(),
                new OrderCreationRequestedEvent(
                        order.getId(),
                        order.getUserId(),
                        order.getOrderItems()
                                .stream()
                                .map(oi -> new OrderItemData(oi.getProductId(), oi.getQuantity()))
                                .collect(Collectors.toCollection(LinkedHashSet::new))));
    }

    public void publishOrderPriceCommittedEvent(Order order) {
        checkAndAdvanceOrder(order);
    }

    public void checkAndAdvanceOrder(Order order) {
        Boolean isReady = sagaTrackerService.checkPrePaymentReadiness(order.getId(), order.getUserId());
        if (Boolean.TRUE.equals(isReady)) {
            publishOrderInitialPaymentRequestedEvent(order);
        } else if (Boolean.FALSE.equals(isReady)) {
            publishCancellationEvent(order);
        }

    }

    private void publishOrderInitialPaymentRequestedEvent(Order order) {
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
        // ... logic tạo và gửi OrderCanceledEvent
    }

    public void publishOrderStockAllocationRequestedEvent(Order order) {

        OrderStockAllocationRequestedEvent event = new OrderStockAllocationRequestedEvent(
                order.getId(),
                order.getUserId());
        kafkaTemplate.send(
                KafkaTopics.SALES_ORDER_COMMAND,
                String.valueOf(event.getOrderId()),
                event);
    }

}
