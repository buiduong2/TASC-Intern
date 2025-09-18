package com.backend.order.dto.req;

import com.backend.common.validation.EnumValue;
import com.backend.order.model.PaymentMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentTransactionReq {

    @EnumValue(enumClass = PaymentMethod.class)
    private String method;
}
