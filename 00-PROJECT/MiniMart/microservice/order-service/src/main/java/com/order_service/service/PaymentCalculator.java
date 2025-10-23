package com.order_service.service;

import com.order_service.model.Payment;
import com.order_service.model.PaymentTransaction;

public interface PaymentCalculator {

    void calculateAmountPaid(long paymentId);

    void calculateAmountPaidAfterRefund(Payment payment, PaymentTransaction paymentTransaction);
}
