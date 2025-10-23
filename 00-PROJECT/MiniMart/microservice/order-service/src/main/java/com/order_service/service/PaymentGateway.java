package com.order_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import com.order_service.dto.res.GatewayResponseData;
import com.order_service.dto.res.PaymentGatewayCreateDTO;
import com.order_service.dto.res.PaymentTransactionDTO;
import com.order_service.enums.IpnResponseType;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentGateway {
    PaymentGatewayCreateDTO createTransaction(String orderInfo, String txnRef, BigDecimal amount,
            HttpServletRequest request);

    GatewayResponseData verifyReturn(Map<String, String> params);

    GatewayResponseData verifyIpn(Map<String, String> params);

    Object getIpnResponse(IpnResponseType responseType);

    PaymentTransactionDTO refund(Long paymentId, Long transactionId, Long adminId);

    GatewayResponseData queryDr(String txnRef, String orderInfo, LocalDateTime createdAt);
}
