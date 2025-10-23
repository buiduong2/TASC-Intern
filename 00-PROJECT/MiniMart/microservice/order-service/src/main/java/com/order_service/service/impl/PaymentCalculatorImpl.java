package com.order_service.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.order_service.enums.PaymentStatus;
import com.order_service.enums.TransactionStatus;
import com.order_service.event.PaymentPaidDomainEvent;
import com.order_service.exception.ErrorCode;
import com.order_service.model.Payment;
import com.order_service.model.PaymentTransaction;
import com.order_service.repository.PaymentRepository;
import com.order_service.service.PaymentCalculator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentCalculatorImpl implements PaymentCalculator {

    private final PaymentRepository paymentRepository;

    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @Override
    public void calculateAmountPaid(long paymentId) {
        Payment payment = paymentRepository.findByidForUpdate(paymentId)
                .orElseThrow(() -> new GenericException(ErrorCode.PAYMENT_NOT_FOUND, paymentId));

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
            eventPublisher.publishEvent(new PaymentPaidDomainEvent(payment));
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
