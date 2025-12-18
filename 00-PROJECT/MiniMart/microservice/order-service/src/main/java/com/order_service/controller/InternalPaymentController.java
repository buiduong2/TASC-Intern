package com.order_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.exception.saga.DuplicateResourceException;
import com.order_service.dto.req.PaymentTransactionReq;
import com.order_service.dto.res.PaymentTransactionDTO;
import com.order_service.model.Payment;
import com.order_service.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InternalPaymentController {

    private final PaymentService paymentService;

    @SuppressWarnings("null")
    public ResponseEntity<?> intialPayment(OrderInitialPaymentRequestedEvent event) {
        try {
            Payment payment = paymentService.processInitialPaymentRequest(event);
            PaymentTransactionReq req = new PaymentTransactionReq();
            req.setMethod(payment.getName().name());
            PaymentTransactionDTO transaction = paymentService.createTransaction(
                    payment.getId(),
                    req,
                    payment.getUserId(),
                    ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                            .getRequest());

            PaymentRecordPreparedEvent preparedEvent = new PaymentRecordPreparedEvent(
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getId(),
                    payment.getAmountTotal(),
                    transaction.getPaymentUrl());

            return ResponseEntity.ok(preparedEvent);
        } catch (DuplicateResourceException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
