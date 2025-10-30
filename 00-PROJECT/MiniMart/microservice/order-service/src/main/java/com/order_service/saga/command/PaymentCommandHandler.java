package com.order_service.saga.command;

import java.util.Optional;

import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.common_kafka.config.KafkaTopics;
import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.exception.saga.DuplicateResourceException;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.order_service.model.Payment;
import com.order_service.saga.PaymentSagaManager;
import com.order_service.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@KafkaListener(topics = KafkaTopics.FINANCE_PAYMENT_COMMAND, groupId = "payment-command-consumer-group")
public class PaymentCommandHandler {

    private final PaymentService paymentService;

    private final PaymentSagaManager paymentSagaManager;

    @KafkaHandler
    public void handleInitialPaymentRequest(OrderInitialPaymentRequestedEvent event) {
        long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();

        log.info("🟢📥 [{}][OrderInitialPaymentRequestedEvent][RECEIVED][ID={}] Processing initial payment", SAGA,
                orderId);

        try {
            Payment result = paymentService.processInitialPaymentRequest(event);
            paymentSagaManager.publishPaymentRecordPreparedEvent(event, result);
            log.info("📤 [{}][PaymentRecordPreparedEvent][PUBLISHED][ID={}] Payment record prepared", SAGA, orderId);

        } catch (DuplicateResourceException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][DUPLICATE_RESOURCE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (Exception e) {
            log.error("❌ [{}][OrderInitialPaymentRequestedEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);
        }
    }

    @KafkaHandler
    public void handleOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();
        log.info("♻️📥 [{}][{}][RECEIVED][ID={}] Received compensation request for order", SAGA, EVENT, orderId);

        try {
            Optional<Payment> paymentOptional = paymentService.processOrderCreationCompensated(event);
            paymentSagaManager.publishPaymentCompensationCompletedEvent(event, paymentOptional);
            log.info(
                    "✅📤 [{}][PaymentCompensationCompletedEvent][PUBLISHED][ID={}] Payment compensation completed successfully",
                    SAGA, orderId);

        } catch (IdempotentEventException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][IDEMPOTENT][ID={}] Already processed, skipping event",
                    SAGA, EVENT, orderId);
        } catch (Exception e) {
            log.error("💥 [{}][{}][ERROR][ID={}] Unexpected error during payment compensation: {}",
                    SAGA, EVENT, orderId, e.getMessage(), e);

        }
    }

    @KafkaHandler
    public void handleOrderCancellationRequested(OrderCancellationRequestedEvent event) {
        try {
            Payment payment = paymentService.processOrderCancellationRequested(event);
            paymentSagaManager.publishPaymentCompensationCompletedEvent(event, Optional.of(payment));
        } catch (IdempotentEventException e) {
            log.info("[PaymentService] Skip duplicate cancel event for orderId={}, reason={}",
                    event.getOrderId(), e.getMessage());
        } catch (Exception e) {
            log.error("[PaymentService] Error handling OrderCancellationRequestedEvent, orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
        }
    }

}
