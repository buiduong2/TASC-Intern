package com.order_service.service;

import java.util.Map;

import com.common_kafka.event.sales.order.OrderCancellationRequestedEvent;
import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.shared.res.SagaResult;
import com.order_service.dto.req.PaymentTransactionReq;
import com.order_service.dto.req.RefundReq;
import com.order_service.dto.res.PaymentAdminDetailDTO;
import com.order_service.dto.res.PaymentSummaryDTO;
import com.order_service.dto.res.PaymentTransactionDTO;
import com.order_service.model.Payment;

import jakarta.servlet.http.HttpServletRequest;

public interface PaymentService {

    SagaResult<Payment> processInitialPaymentRequest(OrderInitialPaymentRequestedEvent event);

    void processOrderCreationCompensated(OrderCreationCompensatedEvent event);

    PaymentAdminDetailDTO findAdminById(long id);

    PaymentTransactionDTO refund(long transactionId, RefundReq req, long userId, HttpServletRequest servletRequest);

    PaymentTransactionDTO queryDr(long transactionId);

    PaymentSummaryDTO findById(long id, Long userId);

    PaymentTransactionDTO createTransaction(long paymentId, PaymentTransactionReq req, Long userId,
            HttpServletRequest request);

    PaymentTransactionDTO verifyReturn(String gateway, Map<String, String> allParams);

    Object handleIpn(String gateway, Map<String, String> allParams);

    void cancel(long paymentId);

    Payment processOrderCancellationRequested(OrderCancellationRequestedEvent event);

}
