package com.common_kafka.event.finance.payment;

import java.math.BigDecimal;

import com.common_kafka.event.shared.AbstractSagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PaymentRecordPreparedEvent extends AbstractSagaEvent {

    private Long paymentId;

    private BigDecimal amountToPay;

    public PaymentRecordPreparedEvent(long orderId, long userId, Long paymentId, BigDecimal amountToPay) {
        super(orderId, userId);
        this.paymentId = paymentId;
        this.amountToPay = amountToPay;
    }
}
