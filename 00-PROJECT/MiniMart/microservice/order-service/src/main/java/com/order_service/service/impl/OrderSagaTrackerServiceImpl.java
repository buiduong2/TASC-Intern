package com.order_service.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.exception.saga.ResourceNotFoundException;
import com.order_service.enums.SagaStepStatus;
import com.order_service.enums.SagaStepType;
import com.order_service.model.OrderSagaTracker;
import com.order_service.repository.OrderSagaTrackerRepository;
import com.order_service.service.OrderSagaTrackerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderSagaTrackerServiceImpl implements OrderSagaTrackerService {

    private final OrderSagaTrackerRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void create(Long orderId) {
        OrderSagaTracker orderSagaProgress = new OrderSagaTracker();

        orderSagaProgress.setOrderId(orderId);

        orderSagaProgress.setUnitPriceConfirmed(SagaStepStatus.NOT_STARTED);
        orderSagaProgress.setStockReserved(SagaStepStatus.NOT_STARTED);
        orderSagaProgress.setPaymentProcessed(SagaStepStatus.NOT_STARTED);
        orderSagaProgress.setStockFulfilled(SagaStepStatus.NOT_STARTED);

        orderSagaProgress.setFailureReason(null);

        repository.save(orderSagaProgress);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void startSteps(long orderId, Collection<SagaStepType> types) {
        OrderSagaTracker ops = repository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderSagaTracker", orderId));

        for (SagaStepType type : types) {
            updateStatus(ops, type, oldstatus -> oldstatus == SagaStepStatus.NOT_STARTED, SagaStepStatus.PENDING);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void startStep(long orderId, SagaStepType type) {
        OrderSagaTracker ops = repository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderSagaTracker", orderId));

        updateStatus(ops, type, oldstatus -> oldstatus == SagaStepStatus.NOT_STARTED, SagaStepStatus.PENDING);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void completeStep(long orderId, SagaStepType type) {
        OrderSagaTracker ops = repository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderSagaTracker", orderId));

        updateStatus(ops, type, oldstatus -> oldstatus == SagaStepStatus.PENDING, SagaStepStatus.PENDING);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void compensatedStep(long orderId, SagaStepType type) {
        OrderSagaTracker ops = repository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderSagaTracker", orderId));

        updateStatus(ops, type, (currentStatus) -> true, SagaStepStatus.COMPENSATED);

    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void failedStep(long orderId, SagaStepType stepType, String reason) {
        OrderSagaTracker ops = repository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderSagaTracker", orderId));

        concatFailureReason(ops, reason);

        updateStatus(ops, stepType, current -> current == SagaStepStatus.PENDING, SagaStepStatus.FAILED);

        repository.save(ops);
    }

    public void updateStatus(
            OrderSagaTracker ops,
            SagaStepType type, Predicate<SagaStepStatus> oldStatusCheck,
            SagaStepStatus newStatus) {

        switch (type) {
            case PAYMENT_PROCESSED:
                if (oldStatusCheck.test(ops.getPaymentProcessed())) {
                    ops.setPaymentProcessed(newStatus);
                }
                break;

            case UNIT_PRICE_CONFIRMED:
                if (oldStatusCheck.test(ops.getUnitPriceConfirmed())) {
                    ops.setUnitPriceConfirmed(newStatus);
                }
                break;

            case STOCK_FULFILLED:
                if (oldStatusCheck.test(ops.getStockFulfilled())) {
                    ops.setStockFulfilled(newStatus);
                }
                break;

            case STOCK_RESERVED:
                if (oldStatusCheck.test(ops.getStockReserved())) {
                    ops.setStockReserved(newStatus);
                }
                break;
            default:
                break;
        }
    }

    private void concatFailureReason(OrderSagaTracker ops, String reason) {
        if (ops.getFailureReason() == null || ops.getFailureReason().isBlank()) {
            ops.setFailureReason(reason);
        } else {
            ops.setFailureReason(ops.getFailureReason() + " - " + reason);
        }
    }

    @Override
    public Boolean checkPrePaymentReadinessOrCancelReadiness(Long orderId, long userId) {
        OrderSagaTracker ops = repository.findByOrderId(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderSagaTracker", orderId));

        if (ops.getUnitPriceConfirmed() == SagaStepStatus.FAILED || ops.getStockReserved() == SagaStepStatus.FAILED) {

            return false; // trigger Compenstation
        }

        if (ops.getUnitPriceConfirmed() == SagaStepStatus.SUCCESS && ops.getStockReserved() == SagaStepStatus.SUCCESS) {
            return true; // Trigger initial Payment
        }

        // Not ready
        return null;

    }

    @Override
    public boolean checkOrderCanceledReadiness(long orderId, long userId) {
        OrderSagaTracker ops = repository.findByOrderId(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderSagaTracker", orderId));

        SagaStepStatus paymentProcessed = ops.getPaymentProcessed();
        SagaStepStatus stockFulfilled = ops.getStockFulfilled();
        SagaStepStatus stockReserved = ops.getStockReserved();

        Set<SagaStepStatus> statuses = new HashSet<>(List.of(paymentProcessed, stockFulfilled, stockReserved));

        if (statuses.contains(SagaStepStatus.PENDING)
                || statuses.contains(SagaStepStatus.SUCCESS)
                || statuses.contains(SagaStepStatus.FAILED)) {

            return false;
        }

        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public OrderSagaTracker findByOrderId(long orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> ResourceNotFoundException.of("OrderSagaTracker", orderId));
    }

}
