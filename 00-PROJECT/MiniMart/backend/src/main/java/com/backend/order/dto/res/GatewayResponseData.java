package com.backend.order.dto.res;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GatewayResponseData {
    private String txnRef;
    private String gatewayTxnId;
    private double amount;
    private String orderInfo;
    private boolean success;
    private boolean issignatureValid;
}
