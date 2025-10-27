package com.order_service.saga;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderCreationRequestedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.sales.order.OrderStockAllocationRequestedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.order_service.model.Order;
import com.order_service.model.OrderSagaTracker;
import com.order_service.repository.OrderRepository;
import com.order_service.service.OrderSagaTrackerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final OrderSagaTrackerService trackerService;

    private final OrderRepository orderRepository;

    public void publishOrderCreationRequestedEvent(Order order) {
        kafkaTemplate.send(
                KafkaTopics.SALES_ORDER_INIT_COMMANDS,
                order.getId().toString(),
                new OrderCreationRequestedEvent(
                        order.getId(),
                        order.getUserId(),
                        toOrderItemData(order)));
    }

    public void tryPublishOrderInitialPaymentEventOrCancel(Order order) {
        checkAndAdvanceOrder(order);
    }

    public void tryPublishCreationCompensatedEvent(long orderId, long userId) {
        Order order = orderRepository.findByIdAndUserIdWithItem(orderId, userId).orElseThrow();
        checkAndAdvanceOrder(order);
    }

    public void checkAndAdvanceOrder(Order order) {
        Boolean isReady = trackerService.checkPrePaymentReadinessOrCancelReadiness(order.getId(), order.getUserId());
        if (Boolean.TRUE.equals(isReady)) {
            publishOrderInitialPaymentRequestedEvent(order);
        } else if (Boolean.FALSE.equals(isReady)) {
            OrderSagaTracker tracker = trackerService.findByOrderId(order.getId());
            publishCreationCompensatedEvent(order, tracker);
        }

    }

    private void publishOrderInitialPaymentRequestedEvent(Order order) {
        log.info("[SAGA][OrderId={}][ACTION=publishPaymentRequested] ✅ Published OrderInitialPaymentRequestedEvent",
                order.getId());

        OrderInitialPaymentRequestedEvent event = new OrderInitialPaymentRequestedEvent(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getPaymentMethod().name());

        kafkaTemplate.send(
                KafkaTopics.FINANCE_PAYMENT_REQUEST,
                String.valueOf(event.getOrderId()),
                event);
    }

    private void publishCreationCompensatedEvent(Order order, OrderSagaTracker ost) {
        log.warn("[SAGA][OrderId={}][ACTION=publishCancellation] ❌ Saga canceled", order.getId());

        OrderCreationCompensatedEvent event = new OrderCreationCompensatedEvent(
                order.getId(),
                order.getUserId(),
                toOrderItemData(order),
                ost.getFailureReason());

        kafkaTemplate.send(
                KafkaTopics.SALES_ORDER_COMPENSATION,
                String.valueOf(event.getOrderId()),
                event);

    }

    public void publishOrderStockAllocationRequestedEvent(Order order) {

        OrderStockAllocationRequestedEvent event = new OrderStockAllocationRequestedEvent(
                order.getId(),
                order.getUserId(),
                toOrderItemData(order));

        kafkaTemplate.send(
                KafkaTopics.SUPPLY_INVENTORY_ALLOCATION,
                String.valueOf(event.getOrderId()),
                event);
    }

    public void publishOrderCancelRequestedEvent(Order order) {
        OrderCancellationRequestedEvent event = new OrderCancellationRequestedEvent(
                order.getId(),
                order.getUserId(),
                toOrderItemData(order));

        kafkaTemplate.send(
                KafkaTopics.SALES_ORDER_CANCEL_COMMANDS,
                String.valueOf(event.getOrderId()),
                event);
    }

    private LinkedHashSet<OrderItemData> toOrderItemData(Order order) {
        return order.getOrderItems()
                .stream()
                .map(oi -> new OrderItemData(oi.getId(), oi.getProductId(), oi.getQuantity()))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

}
