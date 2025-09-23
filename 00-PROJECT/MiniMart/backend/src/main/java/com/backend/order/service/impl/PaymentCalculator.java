package com.backend.order.service.impl;

import java.math.BigDecimal;
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
        payment.setAmountPaid(BigDecimal.ZERO);

        BigDecimal shippingCost = order.getShippingMethod().getCost();
        BigDecimal subTotal = order.getOrderItems().stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        payment.setAmountTotal(shippingCost.add(subTotal));
        return payment;
    }

    @Transactional
    public void calculateAmountPaid(long paymentId) {
        Payment payment = paymentRepository.findByIdForUpdateAmountPaid(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PAYMENT_NOT_FOUND.format(paymentId)));

        if (payment.getTransactions() == null || payment.getTransactions().isEmpty()) {
            payment.setAmountPaid(BigDecimal.ZERO);
            return;
        }

        BigDecimal amountPaid = payment.getTransactions().stream()
                .filter(t -> t.getStatus() == TransactionStatus.SUCCESS)
                .map(PaymentTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        payment.setAmountPaid(amountPaid);

        if (amountPaid.compareTo(payment.getAmountTotal()) < 0) {
            payment.setStatus(PaymentStatus.PARTIAL);
        } else {
            payment.setStatus(PaymentStatus.PAID);
        }

    }

    public void calculateAmountPaidAfterRefund(Payment payment, PaymentTransaction paymentTransaction) {
        BigDecimal newAmountPaid = payment.getAmountPaid()
                .subtract(paymentTransaction.getAmount());

        payment.setAmountPaid(newAmountPaid);
        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setCompletedAt(LocalDateTime.now());
    }
}
