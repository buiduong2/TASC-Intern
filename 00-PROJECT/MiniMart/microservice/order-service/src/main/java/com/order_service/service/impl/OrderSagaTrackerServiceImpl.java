package com.order_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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
    public void startStep(long orderId, SagaStepType stepType) {
        switch (stepType) {
            case UNIT_PRICE_CONFIRMED -> repository.updateUnitPriceConfirmed(orderId, SagaStepStatus.PENDING);
            case STOCK_RESERVED -> repository.updateStockReserved(orderId, SagaStepStatus.PENDING);
            case PAYMENT_PROCESSED -> repository.updatePaymentProcessed(orderId, SagaStepStatus.PENDING);
            case STOCK_FULFILLED -> repository.updateStockFulfilled(orderId, SagaStepStatus.PENDING);
            default -> throw new IllegalArgumentException("Unsupported step type: " + stepType);
        }
    }

    @Override
    public void completeStep(long orderId, SagaStepType stepType, boolean success, String reason) {
        if (success) {
            updateStepStatus(orderId, stepType, SagaStepStatus.SUCCESS);
        } else {
            handleStepFailure(orderId, stepType, reason);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void markSuccessStep(long orderId, SagaStepType stepType) {
        completeStep(orderId, stepType, true, null);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void markFailedStep(long orderId, SagaStepType stepType, String reason) {
        completeStep(orderId, stepType, false, reason);
    }

    private void updateStepStatus(long orderId, SagaStepType stepType, SagaStepStatus status) {
        switch (stepType) {
            case UNIT_PRICE_CONFIRMED -> repository.updateUnitPriceConfirmed(orderId, status);
            case STOCK_RESERVED -> repository.updateStockReserved(orderId, status);
            case PAYMENT_PROCESSED -> repository.updatePaymentProcessed(orderId, status);
            case STOCK_FULFILLED -> repository.updateStockFulfilled(orderId, status);
        }
    }

    private void handleStepFailure(long orderId, SagaStepType stepType, String reason) {
        OrderSagaTracker ops = repository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalStateException("OrderSagaProgress not found for orderId=" + orderId));

        concatFailureReason(ops, reason);

        switch (stepType) {
            case UNIT_PRICE_CONFIRMED -> ops.setUnitPriceConfirmed(SagaStepStatus.FAILED);
            case STOCK_RESERVED -> ops.setStockReserved(SagaStepStatus.FAILED);
            case PAYMENT_PROCESSED -> ops.setPaymentProcessed(SagaStepStatus.FAILED);
            case STOCK_FULFILLED -> ops.setStockFulfilled(SagaStepStatus.FAILED);
        }

        repository.save(ops);
    }

    private void concatFailureReason(OrderSagaTracker ops, String reason) {
        if (ops.getFailureReason() == null || ops.getFailureReason().isBlank()) {
            ops.setFailureReason(reason);
        } else {
            ops.setFailureReason(ops.getFailureReason() + " - " + reason);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public Boolean checkPrePaymentReadinessOrCancelReadiness(Long orderId, long userId) {
        OrderSagaTracker ops = repository.findByOrderIdForUpdate(orderId)
                .orElseThrow(() -> new IllegalStateException("OrderSagaProgress not found for orderId=" + orderId));

        if (ops.getUnitPriceConfirmed() == SagaStepStatus.FAILED || ops.getStockReserved() == SagaStepStatus.FAILED) {

            return false; // trigger Compenstation
        }

        if (ops.getUnitPriceConfirmed() == SagaStepStatus.SUCCESS && ops.getStockReserved() == SagaStepStatus.SUCCESS) {
            return true; // Trigger initial Payment
        }

        // Not ready
        return null;

    }

}
