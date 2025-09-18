package com.backend.order.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentGatewayCreateDTO {
    private String url;
    private String txnRef;
}
