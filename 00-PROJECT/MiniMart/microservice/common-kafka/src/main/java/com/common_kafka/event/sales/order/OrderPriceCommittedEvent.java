package com.common_kafka.event.sales.order;

import java.math.BigDecimal;

import com.common_kafka.event.shared.AbstractSagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderPriceCommittedEvent extends AbstractSagaEvent {
    private BigDecimal totalAmount;

    public OrderPriceCommittedEvent(long orderId, long userId, BigDecimal totalAmount) {
        super(orderId, userId);
        this.totalAmount = totalAmount;
    }

}
