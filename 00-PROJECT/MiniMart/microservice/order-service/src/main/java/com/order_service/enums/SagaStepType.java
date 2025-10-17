package com.order_service.enums;

public enum SagaStepType {
    UNIT_PRICE_CONFIRMED, // Đã update lại UnitPrice của từng OrderItem
    STOCK_RESERVED,  // Stock đã ghi nhận các reservceQuantity
    PAYMENT_PROCESSED, // Việc thanh toán  (ko phải khởi tạo)
    STOCK_FULFILLED // PurchaseItem đã được tạo để tính ra avgCost
}
