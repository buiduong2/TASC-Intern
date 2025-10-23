package com.common_kafka.event.finance.payment;

import com.common_kafka.event.shared.AbstractSagaEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSucceededEvent extends AbstractSagaEvent {

    private long paymentId;

    public PaymentSucceededEvent(long orderId, long userId, long paymentId) {
        super(orderId, userId);
        this.paymentId = paymentId;
    }

}
