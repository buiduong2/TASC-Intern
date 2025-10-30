package com.order_service.service;

import java.util.Collection;

import com.order_service.enums.SagaStepType;
import com.order_service.model.OrderSagaTracker;

public interface OrderSagaTrackerService {

    void create(Long orderId);

    OrderSagaTracker findByOrderId(long orderId);

    void startSteps(long orderId, Collection<SagaStepType> types);

    void startStep(long orderId, SagaStepType type);

    void completeStep(long orderId, SagaStepType stepType);

    void compensatedStep(long orderId, SagaStepType type);

    void failedStep(long orderId, SagaStepType stepType, String reason);

    Boolean checkPrePaymentReadinessOrCancelReadiness(Long orderId, long userId);

    boolean checkOrderCanceledReadiness(long orderId, long userId);
}
