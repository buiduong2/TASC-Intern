package com.order_service.enums;

public enum PaymentStatus {
    PREPARING,
    PENDING,
    PARTIAL,
    PAID,
    REFUND_REQUIRED,
    REFUNDED,
    CANCELLED;

    public static boolean isCreatableTransaction(PaymentStatus status) {
        return status == PENDING || status == PARTIAL;
    }

    public static boolean isEasyCompensation(PaymentStatus status) {
        return status == PREPARING || status == PENDING;
    }

    public static boolean isCompensatedOrCanceled(PaymentStatus status) {
        return status == CANCELLED || status == REFUNDED || status == REFUND_REQUIRED;
    }

    public static boolean isPaid(PaymentStatus status) {
        return status == PARTIAL || status == PAID;
    }

}
