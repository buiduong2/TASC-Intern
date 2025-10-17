package com.order_service.service;

import com.order_service.enums.SagaStepType;

public interface OrderSagaTrackerService {

    void create(Long orderId);

    void startStep(long orderId, SagaStepType stepType);

    void completeStep(long orderId, SagaStepType stepType, boolean success, String reason);

    void markSuccessStep(long orderId, SagaStepType stepType);

    void markFailedStep(long orderId, SagaStepType stepType, String reason);

    Boolean checkPrePaymentReadinessOrCancelReadiness(Long orderId, long userId);

}
