package com.order_service.enums;

public enum OrderStatus {
    PENDING_VALIDATION,
    CONFIRMED,
    SHIPPING,
    COMPLETED,

    PENDING_COMPENSATION_CREATION,

    CANCELED,
}
