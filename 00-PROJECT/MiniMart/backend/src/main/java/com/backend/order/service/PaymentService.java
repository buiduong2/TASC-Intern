package com.backend.order.service;

import java.util.Map;

import com.backend.order.dto.req.PaymentTransactionReq;
import com.backend.order.dto.res.PaymentDTO;
import com.backend.order.dto.res.PaymentTransactionDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {

    public static enum IpnResponseType {
        USER_NOT_REDIRECTED,
        SIGNATURE_NOT_VALID,
        ORDER_NOT_FOUND,
        AMOUNT_NOT_VALID,
        STATUS_NOT_VALID,
        ORDER_CANCELD,
        SUCCESS
    }

    PaymentTransactionDTO createTransaction(long paymentId, PaymentTransactionReq req, long l,
            HttpServletRequest request);

    PaymentDTO findById(long paymentId, long userId);

    PaymentDTO findAdminById(long paymentId);

    PaymentTransactionDTO verifyReturn(String gateway, Map<String, String> allParams);

    Object handleIpn(String gateway, Map<String, String> allParams);

}
