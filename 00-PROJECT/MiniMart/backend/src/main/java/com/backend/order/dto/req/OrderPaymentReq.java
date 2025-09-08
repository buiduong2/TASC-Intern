package com.backend.order.dto.req;

import com.backend.order.model.PaymentMethod;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderPaymentReq {

    private PaymentMethod name;

    private Double amount;

}
