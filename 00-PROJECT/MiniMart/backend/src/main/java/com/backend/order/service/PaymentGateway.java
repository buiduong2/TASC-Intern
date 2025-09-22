package com.backend.order.service;

import java.time.LocalDateTime;
import java.util.Map;

import com.backend.order.dto.res.PaymentGatewayCreateDTO;
import com.backend.order.dto.res.GatewayResponseData;
import com.backend.order.dto.res.PaymentTransactionDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentGateway {
    PaymentGatewayCreateDTO createTransaction(String orderInfo, String txnRef, double amount,
            HttpServletRequest request);

    GatewayResponseData verifyReturn(Map<String, String> params);

    GatewayResponseData verifyIpn(Map<String, String> params);

    Object getIpnResponse(PaymentService.IpnResponseType responseType);

    PaymentTransactionDTO refund(Long paymentId, Long transactionId, Long adminId);

    GatewayResponseData queryDr(String txnRef, String orderInfo,LocalDateTime createdAt);
}
