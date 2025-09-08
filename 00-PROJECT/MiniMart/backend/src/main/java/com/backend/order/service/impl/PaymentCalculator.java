package com.backend.order.service.impl;

import org.springframework.stereotype.Service;

import com.backend.order.model.Order;
import com.backend.order.model.OrderItem;
import com.backend.order.model.Payment;
import com.backend.order.model.PaymentMethod;
import com.backend.order.model.PaymentStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentCalculator {
    
    public Payment calculate(Order order, PaymentMethod paymentMethod) {
        Payment payment = new Payment();
        payment.setName(paymentMethod);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountPaid(0);

        double shippingCost = order.getShippingMethod().getCost();
        double subTotal = order.getOrderItems().stream().mapToDouble(OrderItem::getTotalPrice).sum();

        payment.setAmountDue(shippingCost + subTotal);
        return payment;
    }
}
