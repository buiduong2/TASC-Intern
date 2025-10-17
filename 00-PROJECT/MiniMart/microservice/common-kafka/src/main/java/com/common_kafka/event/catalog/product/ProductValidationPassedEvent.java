package com.common_kafka.event.catalog.product;

import java.util.Set;

import com.common_kafka.event.shared.AbstractSagaEvent;
import com.common_kafka.event.shared.dto.ValidatedItemSnapshot;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductValidationPassedEvent extends AbstractSagaEvent {

    private Set<ValidatedItemSnapshot> validatedItems;

    public ProductValidationPassedEvent(long orderId, long userId, Set<ValidatedItemSnapshot> validatedItems) {
        super(orderId, userId);
        this.validatedItems = validatedItems;
    }

}
