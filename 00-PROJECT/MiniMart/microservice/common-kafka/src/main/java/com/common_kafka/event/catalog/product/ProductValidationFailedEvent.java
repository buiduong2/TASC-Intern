package com.common_kafka.event.catalog.product;

import java.util.Set;

import com.common_kafka.event.shared.AbstractSagaEvent;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductValidationFailedEvent extends AbstractSagaEvent {

    private String reason; // 404 -

    private Set<Long> failedProdutIds;

    public ProductValidationFailedEvent(long orderId, long userId, String reason, Set<Long> failedProdutIds) {
        super(orderId, userId);
        this.reason = reason;
        this.failedProdutIds = failedProdutIds;
    }

}
