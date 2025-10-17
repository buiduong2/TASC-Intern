package com.common_kafka.event.sales.order;

import java.math.BigDecimal;

import com.common_kafka.event.shared.AbstractSagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderInitialPaymentRequestedEvent extends AbstractSagaEvent {

    private BigDecimal totalAmount;
    private String paymentMethod;

    public OrderInitialPaymentRequestedEvent(long orderId, long userId, BigDecimal totalAmount, String paymentMethod) {
        super(orderId, userId);
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
    }

}
