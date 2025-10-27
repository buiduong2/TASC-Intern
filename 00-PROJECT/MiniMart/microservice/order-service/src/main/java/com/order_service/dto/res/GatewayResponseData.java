package com.order_service.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class GatewayResponseData {
    private LocalDateTime paidAt;
    private String txnRef;
    private String gatewayTxnId;
    private BigDecimal amount;
    private String orderInfo;
    private boolean success;
    private boolean issignatureValid;
}
