package com.backend.order.dto.req;

import java.math.BigDecimal;

import com.backend.common.validation.EnumValue;
import com.backend.order.model.PaymentMethod;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RefundReq {

    @Positive
    private BigDecimal amount;

    @NotEmpty
    private String reason;

    @EnumValue(enumClass = PaymentMethod.class)
    private String method;
}
