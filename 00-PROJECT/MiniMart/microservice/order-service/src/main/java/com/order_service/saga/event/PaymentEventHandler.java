package com.order_service.saga.event;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.finance.payment.PaymentCompensationCompletedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.finance.payment.PaymentRefundedEvent;
import com.common_kafka.event.finance.payment.PaymentPaidEvent;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.order_service.enums.SagaStepType;
import com.order_service.model.Order;
import com.order_service.saga.OrderSagaManager;
import com.order_service.service.OrderSagaTrackerService;
import com.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@KafkaListener(topics = KafkaTopics.FINANCE_PAYMENT_EVENTS, groupId = "order-payment-record-group")
@Slf4j
public class PaymentEventHandler {

    private final OrderService orderService;
    private final OrderSagaTrackerService orderSagaTrackerService;
    private final OrderSagaManager orderSagaManager;

    @KafkaHandler
    public void handlePaymentRecordCreated(PaymentRecordPreparedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();

        log.info("🟢📥 [{}][PaymentRecordPreparedEvent][RECEIVED][ID={}] Payment prepared", SAGA, orderId);

        try {
            Order order = orderService.processPaymentRecordCreated(event);

            orderSagaTrackerService.completeStep(event.getOrderId(), SagaStepType.PAYMENT_PROCESSED);
            orderSagaTrackerService.startStep(orderId, SagaStepType.STOCK_FULFILLED);

            orderSagaManager.publishOrderStockAllocationRequestedEvent(order);
            log.info(
                    "📤 [{}][OrderStockAllocationRequestedEvent][PUBLISHED][ID={}] Stock allocation triggered after paymentId={}",
                    SAGA, orderId, order.getPaymentId());

        } catch (ResourceNotFoundException e) {
            log.error("❌ [{}][{}][NOT_FOUND][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][INVALID_STATE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (IdempotentEventException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][IDEMPOTENT][ID={}] Already processed, skip event",
                    SAGA, EVENT, orderId);
        } catch (Exception e) {
            log.error("❌ [{}][PaymentRecordPreparedEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);
        }

    }

    @KafkaHandler
    public void handlePaymentCompensationCompletedEvent(PaymentCompensationCompletedEvent event) {
        orderSagaTrackerService.compensatedStep(event.getOrderId(), SagaStepType.PAYMENT_PROCESSED);
    }

    @KafkaHandler
    public void handlePaymentPaidEvent(PaymentPaidEvent event) {
        orderSagaTrackerService.completeStep(event.getOrderId(), SagaStepType.PAYMENT_PROCESSED);
        orderService.processPaymentPaidEvent(event);
    }

    @KafkaHandler
    public void handlePaymentRefunded(PaymentRefundedEvent event) {

        final long orderId = event.getOrderId();
        final String SAGA = "Payment_PAID";
        final String EVENT = event.getClass().getSimpleName();

        try {
            orderService.processPaymentRefunded(event);
        } catch (IdempotentEventException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][IDEMPOTENT][ID={}] Already processed, skip event",
                    SAGA, EVENT, orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
