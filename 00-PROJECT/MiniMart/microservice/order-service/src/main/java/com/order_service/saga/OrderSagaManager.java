package com.order_service.saga;

import java.util.LinkedHashSet;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.sales.order.OrderProductValidationRequestedEvent;
import com.common_kafka.event.sales.order.OrderStockAllocationRequestedEvent;
import com.common_kafka.event.sales.order.OrderStockReservationRequestedEvent;
import com.common_kafka.event.shared.dto.OrderItemData;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.order_service.enums.SagaStepStatus;
import com.order_service.model.Order;
import com.order_service.model.OrderSagaTracker;
import com.order_service.repository.OrderSagaTrackerRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderSagaManager {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final OrderSagaTrackerRepository trackerRepository;

    public void publishOrderProductValidationRequestedEvent(Order order) {
        kafkaTemplate.send(
                KafkaTopics.CATALOG_PRODUCT_VALIDATION,
                order.getId().toString(),
                createOrderProductValidationRequestedEvent(order));
    }

    public OrderProductValidationRequestedEvent createOrderProductValidationRequestedEvent(Order order) {
        return new OrderProductValidationRequestedEvent(
                order.getId(),
                order.getUserId(),
                toOrderItemData(order));
    }

    public void publishOrderStockReservationRequestedEvent(Order order) {
        OrderStockReservationRequestedEvent event = createOrderStockReservationRequestedEvent(order);

        kafkaTemplate.send(
                KafkaTopics.SUPPLY_INVENTORY_RESERVATION,
                order.getId().toString(),
                event);

    }

    public OrderStockReservationRequestedEvent createOrderStockReservationRequestedEvent(Order order) {
        OrderStockReservationRequestedEvent event = new OrderStockReservationRequestedEvent(
                order.getId(),
                order.getUserId(),
                toOrderItemData(order));
        return event;
    }

    public void publishOrderInitialPaymentRequestedEvent(Order order) {
        log.info("[SAGA][OrderId={}][ACTION=publishPaymentRequested] ✅ Published OrderInitialPaymentRequestedEvent",
                order.getId());

        OrderInitialPaymentRequestedEvent event = createOrderIntialPaymentRequestedEvent(order);

        kafkaTemplate.send(
                KafkaTopics.FINANCE_PAYMENT_COMMAND,
                String.valueOf(event.getOrderId()),
                event);
    }

    public OrderInitialPaymentRequestedEvent createOrderIntialPaymentRequestedEvent(Order order) {
        return new OrderInitialPaymentRequestedEvent(
                order.getId(),
                order.getUserId(),
                order.getTotalPrice(),
                order.getPaymentMethod().name());

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void publishCreationCompensatedEvent(Order order) {
        OrderSagaTracker ost = trackerRepository.findByOrderIdForUpdate(order.getId())
                .orElseThrow(() -> ResourceNotFoundException.of(
                        "OrderSagaTracker",
                        order.getId()));

        OrderCreationCompensatedEvent event = new OrderCreationCompensatedEvent(
                order.getId(),
                order.getUserId(),
                toOrderItemData(order),
                ost.getFailureReason());

        SagaStepStatus paymentProcessed = ost.getPaymentProcessed();
        SagaStepStatus stockReserved = ost.getStockReserved();

        if (needCompensate(stockReserved)) {
            kafkaTemplate.send(
                    KafkaTopics.SUPPLY_INVENTORY_RESERVATION,
                    String.valueOf(event.getOrderId()),
                    event);
        }

        if (needCompensate(paymentProcessed)) {
            kafkaTemplate.send(
                    KafkaTopics.FINANCE_PAYMENT_COMMAND,
                    String.valueOf(event.getOrderId()),
                    event);
        }

    }

    private boolean needCompensate(SagaStepStatus status) {
        if (status == SagaStepStatus.NOT_STARTED) {
            return false;
        }
        if (status == SagaStepStatus.COMPENSATED) {
            return false;
        }

        return true;
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
                KafkaTopics.SUPPLY_INVENTORY_ALLOCATION,
                String.valueOf(event.getOrderId()),
                event);

        kafkaTemplate.send(
                KafkaTopics.SUPPLY_INVENTORY_RESERVATION,
                String.valueOf(event.getOrderId()),
                event);

        kafkaTemplate.send(
                KafkaTopics.FINANCE_PAYMENT_COMMAND,
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
