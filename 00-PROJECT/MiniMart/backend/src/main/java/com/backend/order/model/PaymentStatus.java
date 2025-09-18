package com.backend.order.model;

import lombok.Getter;

@Getter
public enum PaymentStatus {
    PENDING(true),
    PARTIAL(true),
    PAID(false),
    REFUND_PENDING(true),
    REFUNDED(false),
    CANCELLED(false);

    private PaymentStatus(boolean isPending) {
        this.isPending = isPending;
    }

    boolean isPending;

}
