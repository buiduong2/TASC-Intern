package com.order_service.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSyncStepFailedException extends RuntimeException {
    private final String step;
    private final String reason;
    private final long orderId;

    public OrderSyncStepFailedException(String step, String reason, Long orderId) {
        super("Order sync failed at step " + step + ": " + reason);
        this.step = step;
        this.reason = reason;
        this.orderId = orderId;
    }

}
