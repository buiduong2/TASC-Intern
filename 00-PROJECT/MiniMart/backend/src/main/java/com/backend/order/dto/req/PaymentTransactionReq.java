package com.backend.order.dto.req;

import com.backend.common.validation.EnumValue;
import com.backend.order.model.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTransactionReq {

    @NotNull
    @EnumValue(enumClass = PaymentMethod.class)
    private String method;
}
