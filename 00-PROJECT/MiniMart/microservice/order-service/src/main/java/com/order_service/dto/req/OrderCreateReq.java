package com.order_service.dto.req;

import java.util.LinkedHashSet;

import com.common.validation.EnumValue;
import com.order_service.enums.PaymentMethod;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreateReq {

    private String message;

    @NotNull
    private Long shippingMethodId;

    @NotNull
    @EnumValue(PaymentMethod.class)
    private String paymentMethod;

    @Valid
    @NotNull
    private AddressUpdateReq address;

    @NotEmpty(groups = FromReqGroup.class)
    @Null(groups = FromCartGroup.class)
    @Valid
    private LinkedHashSet<OrderItemCreateReq> orderItems;

}
