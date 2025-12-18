package com.order_service.saga.handler.impl;

import java.util.Map;

import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.sales.order.OrderStockReservationRequestedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.order_service.client.InventoryClient;
import com.order_service.client.PaymentClient;
import com.order_service.client.ProductClient;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.exception.OrderSyncStepFailedException;
import com.order_service.model.Order;
import com.order_service.repository.OrderRepository;
import com.order_service.saga.OrderSagaManager;
import com.order_service.saga.handler.OrderCreationCompensationHandler;
import com.order_service.saga.handler.OrderCreationSyncHandler;
import com.order_service.service.OrderCreationFlowService;
import com.order_service.utils.FeignSafeExecutor;
import com.order_service.utils.SafeResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderCreationSyncHandlerImpl implements OrderCreationSyncHandler {

    private final OrderCreationFlowService creationFlowService;

    private final OrderCreationCompensationHandler creationCompensationHandler;

    private final ProductClient productClient;

    private final PaymentClient paymentClient;

    private final InventoryClient inventoryClient;

    private final OrderSagaManager sagaManager;

    private final FeignSafeExecutor feignSafeExecutor;

    private final OrderRepository orderRepository;

    @Override
    public Pair<Order, String> create(Order order) {

        try {
            order = this.step1_validateOrder(order);

            order = this.step2_reservateStock(order);

            Pair<Order, String> pair = this.step3_initialPayment(order);
            order = pair.getFirst();
            String paymentUrl = pair.getSecond();

            sagaManager.publishOrderStockAllocationRequestedEvent(order);

            return Pair.of(
                    creationFlowService.getOrderSummaryDTO(order.getId(), order.getUserId()),
                    paymentUrl);
        } catch (Exception e) {
            e.printStackTrace();
            sagaManager.publishCreationCompensatedEvent(
                    orderRepository.findByIdAndUserIdWithItem(order.getId(), order.getUserId()).get());

            throw e;
        }

    }

    private Order step1_validateOrder(Order order) {
        creationFlowService.handleOrderPrepared(new OrderPreparedDomainEvent(order));

        var validateReqeust = sagaManager.createOrderProductValidationRequestedEvent(order);

        SafeResponse<ProductValidationPassedEvent, ProductValidationFailedEvent> validateRes = feignSafeExecutor
                .call(
                        () -> productClient.validateOrder(validateReqeust),
                        Map.entry(HttpStatus.BAD_GATEWAY.value(), ProductValidationFailedEvent.class));

        order = feignSafeExecutor.handleResponse(validateRes,
                () -> creationFlowService.processProductValidationPassedEvent(validateRes.getSuccessBody()),
                () -> creationCompensationHandler.processProductValidationFailedEvent(validateRes.getFailureBody()));

        if (!Boolean.TRUE.equals(validateRes.getSuccess())) {
            throw new OrderSyncStepFailedException(
                    "PRODUCT_VALIDATION",
                    validateRes.getFailureBody().getReason(),
                    order.getId());
        }

        return order;

    }

    private Order step2_reservateStock(Order order) {
        OrderStockReservationRequestedEvent reservationReq = sagaManager
                .createOrderStockReservationRequestedEvent(order);
        SafeResponse<InventoryReservedConfirmedEvent, InventoryReservationFailedEvent> reservationRes = feignSafeExecutor
                .call(
                        () -> inventoryClient.reservateOrder(reservationReq),
                        Map.entry(HttpStatus.BAD_REQUEST.value(), InventoryReservationFailedEvent.class));

        order = feignSafeExecutor.handleResponse(reservationRes,
                () -> creationFlowService.processInventoryReservedConfirmed(reservationRes.getSuccessBody()),
                () -> creationCompensationHandler.processInventoryReservedFailed(reservationRes.getFailureBody()));

        if (!Boolean.TRUE.equals(reservationRes.getSuccess())) {
            throw new OrderSyncStepFailedException(
                    "INVENTORY_RESERVATION",
                    reservationRes.getFailureBody().getReason(),
                    order.getId());
        }
        return order;

    }

    private Pair<Order, String> step3_initialPayment(Order order) {
        var initialPaymentReq = sagaManager.createOrderIntialPaymentRequestedEvent(order);
        SafeResponse<PaymentRecordPreparedEvent, Void> intialPaymentRes = feignSafeExecutor.call(
                () -> paymentClient.initialPayment(initialPaymentReq), Map.entry(0, Void.class));

        order = feignSafeExecutor.handleResponse(intialPaymentRes,
                () -> creationFlowService.processPaymentRecordCreated(intialPaymentRes.getSuccessBody()),
                () -> null);

        if (!Boolean.TRUE.equals(intialPaymentRes.getSuccess())) {
            throw new OrderSyncStepFailedException(
                    "PAYMENT_INITIALIZATION",
                    "Cannot create payment record",
                    order.getId());
        }

        return Pair.of(order, intialPaymentRes.getSuccessBody().getPaymentUrl());
    }

}
