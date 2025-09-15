package com.backend.order.dto.res;

import java.time.LocalDateTime;

import com.backend.common.validation.EnumValue;
import com.backend.order.model.OrderStatus;
import com.backend.order.model.PaymentMethod;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderFilter {

    @Positive
    private Long id;

    @EnumValue(enumClass = OrderStatus.class)
    private String status;

    @EnumValue(enumClass = PaymentMethod.class)
    private String paymentMethod;

    @Positive
    private Long shippingMethodId;

    @Positive
    private Long customerId;

    @Past
    private LocalDateTime createdFrom;

    @Past
    private LocalDateTime createdTo;

    @Positive
    private Double minTotalPrice;

    @Positive
    private Double maxTotalPrice;

    @Positive
    private Double minTotalCost;

    @Positive
    private Double maxTotalCost;
}
