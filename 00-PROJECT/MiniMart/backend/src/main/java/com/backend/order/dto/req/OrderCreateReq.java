package com.backend.order.dto.req;

import java.util.LinkedHashSet;

import com.backend.order.model.PaymentMethod;

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
    private PaymentMethod paymentMethod;

    @Valid
    private OrderAddressReq address;

    @NotEmpty(groups = FromReqGroup.class)
    @Null(groups = FromCartGroup.class)
    @Valid
    private LinkedHashSet<OrderItemReq> orderItems;

}
