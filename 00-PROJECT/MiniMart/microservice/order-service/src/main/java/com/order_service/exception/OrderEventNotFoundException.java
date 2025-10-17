package com.order_service.exception;

import java.text.MessageFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderEventNotFoundException extends RuntimeException {

    public OrderEventNotFoundException(long orderId, long userId) {
        super(MessageFormat.format(
                "FATAL Order with orderId = {} AND userId = {} is not found when Order saga progressing ", orderId,
                userId));
    }
}
