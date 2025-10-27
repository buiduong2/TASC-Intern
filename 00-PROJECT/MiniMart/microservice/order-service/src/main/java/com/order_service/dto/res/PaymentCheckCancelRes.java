package com.order_service.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentCheckCancelRes {
    private long paymentId;
    private long userId;
    private boolean isValid;
    private String message;

}
