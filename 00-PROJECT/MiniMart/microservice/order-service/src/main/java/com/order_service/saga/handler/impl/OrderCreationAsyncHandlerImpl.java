package com.order_service.saga.handler.impl;

import org.springframework.stereotype.Service;

import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.InvalidStateException;
import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.model.Order;
import com.order_service.saga.OrderSagaManager;
import com.order_service.saga.handler.OrderCreationAsyncHandler;
import com.order_service.service.OrderCreationFlowService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderCreationAsyncHandlerImpl implements OrderCreationAsyncHandler {

    private final OrderSagaManager sagaManager;
    private final OrderCreationFlowService creationFlowService;
    private final String SAGA = "ORDER_CREATE";

    // 1. Begin Saga -> Validate Product
    @Override
    public void handleOrderPrepared(OrderPreparedDomainEvent event) {
        Order order = event.getOrder();
        long orderId = order.getId();

        log.info("🟢📥 [{}][OrderPreparedDomainEvent][RECEIVED][ID={}] Saga started", SAGA, orderId);

        try {
            creationFlowService.handleOrderPrepared(event);
            sagaManager.publishOrderProductValidationRequestedEvent(order);
            log.info("📤 [{}][ProductValidationRequestedEvent][PUBLISHED][ID={}] Request product validation", SAGA,
                    orderId);
        } catch (Exception e) {
            log.error("❌ [{}][OrderPreparedDomainEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);

        }

    }

    // 2. Validate Product -> Reservation Inventory
    @Override
    public Order processProductValidationPassedEvent(ProductValidationPassedEvent event) {
        final long orderId = event.getOrderId();
        final String EVENT = event.getClass().getSimpleName();

        log.info("🟢📥 [{}][ProductValidationPassedEvent][RECEIVED][ID={}] Product validation passed", SAGA, orderId);

        try {
            Order order = creationFlowService.processProductValidationPassedEvent(event);
            sagaManager.publishOrderStockReservationRequestedEvent(order);
            log.info("📤 [{}][InventoryReserveRequestedEvent][PUBLISHED][ID={}] Request stock reservation", SAGA,
                    orderId);
            return order;

        } catch (ResourceNotFoundException e) {
            log.error("❌ [{}][{}][NOT_FOUND][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][INVALID_STATE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (IdempotentEventException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][IDEMPOTENT][ID={}] Already processed, skip event",
                    SAGA, EVENT, orderId);
        } catch (Exception e) {
            log.error("🔥 [{}][{}][UNEXPECTED][ID={}] {}", SAGA, EVENT, orderId, e.getMessage(), e);
        }
        return null;
    }

    // 3. Reservation Inventory -> Initial PaymentRecord
    @Override
    public Order processInventoryReservedConfirmed(InventoryReservedConfirmedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();

        log.info("🟢📥 [{}][InventoryReservedConfirmedEvent][RECEIVED][ID={}] Stock reserved successfully", SAGA,
                orderId);

        try {
            Order order = creationFlowService.processInventoryReservedConfirmed(event);

            sagaManager.publishOrderInitialPaymentRequestedEvent(order);
            log.info("📤 [{}][OrderInitialPaymentRequestedEvent][PUBLISHED][ID={}] Request initial payment", SAGA,
                    orderId);
        } catch (ResourceNotFoundException e) {
            log.error("❌ [{}][{}][NOT_FOUND][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][INVALID_STATE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (Exception e) {
            log.error("❌ [{}][InventoryReservedConfirmedEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);

        }
        return null;
    }

    // 3. Initial PaymentRecord -> InventoryStockAllocation
    @Override
    public Order processPaymentRecordCreated(PaymentRecordPreparedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();

        log.info("🟢📥 [{}][PaymentRecordPreparedEvent][RECEIVED][ID={}] Payment prepared", SAGA, orderId);

        try {
            Order order = creationFlowService.processPaymentRecordCreated(event);

            sagaManager.publishOrderStockAllocationRequestedEvent(order);
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
        return null;
    }

    // 4.InventoryStockAllocation -> Complete
    @Override
    public Order processInventoryAllocationConfirmed(InventoryAllocationConfirmedEvent event) {
        final long orderId = event.getOrderId();
        final String SAGA = "ORDER_CREATE";
        final String EVENT = event.getClass().getSimpleName();

        log.info("🏁📥 [{}][InventoryAllocationConfirmedEvent][RECEIVED][ID={}] Stock allocated, saga complete", SAGA,
                orderId);

        try {
            creationFlowService.processInventoryAllocationConfirmed(event);
            log.info("🏁📤 [{}][OrderCreatedEvent][PUBLISHED][ID={}] Saga completed successfully ✅", SAGA, orderId);

        } catch (ResourceNotFoundException e) {
            log.error("❌ [{}][{}][NOT_FOUND][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (InvalidStateException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][INVALID_STATE][ID={}] {}", SAGA, EVENT, orderId, e.getMessage());
        } catch (IdempotentEventException e) {
            log.warn("⚠️ [{}][{}][SKIPPED][IDEMPOTENT][ID={}] Already processed, skip event",
                    SAGA, EVENT, orderId);
        } catch (Exception e) {
            log.error("❌ [{}][InventoryAllocationConfirmedEvent][ERROR][ID={}] {}", SAGA, orderId, e.getMessage(), e);
        }
        return null;
    }

}
