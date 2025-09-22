package com.backend.order.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.common.utils.ErrorCode;
import com.backend.order.model.Order;
import com.backend.order.model.OrderItem;
import com.backend.order.model.Payment;
import com.backend.order.model.PaymentMethod;
import com.backend.order.model.PaymentStatus;
import com.backend.order.model.PaymentTransaction;
import com.backend.order.model.TransactionStatus;
import com.backend.order.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentCalculator {

    private final PaymentRepository paymentRepository;

    public Payment calculate(Order order, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setName(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountPaid(0);

        double shippingCost = order.getShippingMethod().getCost();
        double subTotal = order.getOrderItems().stream().mapToDouble(OrderItem::getTotalPrice).sum();

        payment.setAmountTotal(shippingCost + subTotal);
        return payment;
    }

    @Transactional
    public void calculateAmountPaid(long paymentId) {
        Payment payment = paymentRepository.findByIdForUpdateAmountPaid(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PAYMENT_NOT_FOUND.format(paymentId)));

        if (payment.getTransactions() == null || payment.getTransactions().isEmpty()) {
            payment.setAmountPaid(0d);
            return;
        }

        double amountPaid = payment.getTransactions()
                .stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
                .mapToDouble(PaymentTransaction::getAmount)
                .sum();

        payment.setAmountPaid(amountPaid);

        if (amountPaid < payment.getAmountTotal()) {
            payment.setStatus(PaymentStatus.PARTIAL);
        } else {
            payment.setStatus(PaymentStatus.PAID);
        }

    }

    public void calculateAmountPaidAfterRefund(Payment payment, PaymentTransaction paymentTransaction) {
        payment.setAmountPaid(payment.getAmountPaid() - paymentTransaction.getAmount());
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setCompletedAt(LocalDateTime.now());

    }
}
