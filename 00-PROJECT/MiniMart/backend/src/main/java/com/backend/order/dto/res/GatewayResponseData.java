package com.backend.order.dto.res;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GatewayResponseData {
    private String txnRef;
    private String gatewayTxnId;
    private BigDecimal amount;
    private String orderInfo;
    private boolean success;
    private boolean issignatureValid;
}
