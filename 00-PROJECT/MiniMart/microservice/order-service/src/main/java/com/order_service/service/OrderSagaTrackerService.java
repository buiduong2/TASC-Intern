package com.order_service.service;

import com.order_service.enums.SagaStepType;
import com.order_service.model.OrderSagaTracker;

public interface OrderSagaTrackerService {

    void create(Long orderId);

    void startStep(long orderId, SagaStepType stepType);

    void completeStep(long orderId, SagaStepType stepType, boolean success, String reason);

    void markSuccessStep(long orderId, SagaStepType stepType);

    void markFailedStep(long orderId, SagaStepType stepType, String reason);

    OrderSagaTracker findByOrderId(long orderId);

    Boolean checkPrePaymentReadinessOrCancelReadiness(Long orderId, long userId);

    boolean checkCancelRediness(long orderId, long userId);

    void markCompensated(long orderId, SagaStepType type);

    boolean checkOrderCanceledReadiness(long orderId, long userId);

}
