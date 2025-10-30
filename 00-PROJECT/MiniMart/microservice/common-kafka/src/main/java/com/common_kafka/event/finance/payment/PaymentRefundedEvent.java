package com.common_kafka.event.finance.payment;

import com.common_kafka.event.shared.AbstractSagaEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRefundedEvent extends AbstractSagaEvent {
    private long paymentId;

    public PaymentRefundedEvent(long orderId, long userId, long paymentId) {
        super(orderId, userId);
        this.paymentId = paymentId;
    }
}
