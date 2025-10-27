package com.common_kafka.event.finance.payment;

import java.math.BigDecimal;

import com.common_kafka.event.shared.AbstractSagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentCompensationCompletedEvent extends AbstractSagaEvent {

    private Long paymentId;

    private String paymentStatus;
    private BigDecimal amountPaid;

    public PaymentCompensationCompletedEvent(long orderId, long userId, Long paymentId, String paymentStatus,
            BigDecimal amountPaid) {
        super(orderId, userId);
        this.paymentId = paymentId;
        this.paymentStatus = paymentStatus;
        this.amountPaid = amountPaid;
    }

}
