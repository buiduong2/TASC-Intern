package com.order_service.model;

import com.order_service.enums.SagaStepStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class OrderSagaTracker {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private Long orderId;

    @Enumerated(EnumType.STRING)
    private SagaStepStatus unitPriceConfirmed;

    @Enumerated(EnumType.STRING)
    private SagaStepStatus stockReserved;

    @Enumerated(EnumType.STRING)
    private SagaStepStatus paymentProcessed;

    @Enumerated(EnumType.STRING)
    private SagaStepStatus stockFulfilled;

    private String failureReason;

}
