package com.order_service.dto.req;

import com.common.validation.EnumValue;
import com.order_service.enums.PaymentMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTransactionReq {

    @NotNull
    @EnumValue(PaymentMethod.class)
    private String method;
}
