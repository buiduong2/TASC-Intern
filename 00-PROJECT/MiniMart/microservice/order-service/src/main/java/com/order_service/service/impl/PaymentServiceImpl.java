package com.order_service.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.common_kafka.event.sales.order.OrderCreationCompensatedEvent;
import com.common_kafka.event.sales.order.OrderInitialPaymentRequestedEvent;
import com.common_kafka.event.shared.helper.SagaResultUtils;
import com.common_kafka.event.shared.res.SagaResult;
import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;
import com.order_service.model.Payment;
import com.order_service.repository.PaymentRepository;
import com.order_service.service.PaymentService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repository;

    @Transactional
    @Override
    public SagaResult<Payment> processInitialPaymentRequest(OrderInitialPaymentRequestedEvent event) {
        return SagaResultUtils.execute(() -> {
            long orderId = event.getOrderId();
            long userId = event.getUserId();

            Payment payment = new Payment();
            payment.setOrderId(orderId);
            payment.setName(PaymentMethod.valueOf(event.getPaymentMethod()));
            payment.setStatus(PaymentStatus.PENDING);
            payment.setAmountTotal(event.getTotalAmount());
            payment.setUserId(userId);
            repository.save(payment);

            return payment;
        });
    }

    @Transactional
    @Override
    public void processOrderCreationCompensated(OrderCreationCompensatedEvent event) {
        Payment payment = repository.findByOrderIdAndUserId(event.getOrderId(), event.getUserId())
                .orElseGet(() -> null);

        if (payment == null || payment.getStatus().equals(PaymentStatus.CANCELLED)) {
            return;
        }

        payment.setStatus(PaymentStatus.CANCELLED);

        return;
    }

}
