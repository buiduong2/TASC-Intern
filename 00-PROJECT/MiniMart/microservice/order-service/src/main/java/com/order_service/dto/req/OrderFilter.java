package com.order_service.dto.req;

import com.common.validation.EnumValue;
import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentMethod;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderFilter {

    @Positive
    private Long id;

    @EnumValue(OrderStatus.class)
    private String status;

    @EnumValue(PaymentMethod.class)
    private String paymentMethod;

    @Positive
    private Long shippingMethodId;

    @Positive
    private Long userId;

}
