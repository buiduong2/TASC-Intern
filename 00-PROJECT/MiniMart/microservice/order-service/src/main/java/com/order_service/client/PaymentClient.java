package com.order_service.client;

import java.util.Collections;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.order_service.controller.InternalPaymentController;

import feign.FeignException;
import feign.Request;
import feign.Request.HttpMethod;
import feign.Response;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentClient {

    private final InternalPaymentController controller;

    public PaymentRecordPreparedEvent initialPayment(OrderInitialPaymentRequestedEvent event) {
        try {
            ResponseEntity<?> res = controller.intialPayment(event);
            if (res.getStatusCode().is2xxSuccessful()) {
                return (PaymentRecordPreparedEvent) res.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        throw FeignException.InternalServerError.errorStatus("GET", Response.builder()
                .request(Request.create(HttpMethod.POST, "", Collections.emptyMap(), null, null, null))
                .build());
    }
}
