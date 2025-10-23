package com.order_service.saga.handler;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.order_service.event.PaymentPaidDomainEvent;
import com.order_service.model.Payment;
import com.order_service.saga.PaymentSagaManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentPaidHandler {

    private final PaymentSagaManager sagaManager;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePaymentPaid(PaymentPaidDomainEvent event) {
        Payment payment = event.getPayment();
        sagaManager.publishPaymentSucceededEvent(payment);
    }
}
