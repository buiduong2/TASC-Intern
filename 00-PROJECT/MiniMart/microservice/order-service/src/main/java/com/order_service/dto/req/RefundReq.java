package com.order_service.dto.req;

import java.math.BigDecimal;

import com.common.validation.EnumValue;
import com.order_service.enums.PaymentMethod;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class RefundReq {

    @Positive
    private BigDecimal amount;

    @NotEmpty
    private String reason;

    @EnumValue(PaymentMethod.class)
    private String method;
}
