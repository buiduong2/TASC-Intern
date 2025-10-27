package com.order_service.event;

import com.order_service.model.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PaymentCanceledDomainEvent {

    private Payment payment;

}
