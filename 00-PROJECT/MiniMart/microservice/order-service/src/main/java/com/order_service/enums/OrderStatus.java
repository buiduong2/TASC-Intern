package com.order_service.enums;

public enum OrderStatus {
    VALIDATING, // Đang chờ Product + Stock xác thực
    VALIDATED, // Cả hai xác thực xong, đủ điều kiện thanh toán
    CONFIRMED, // Đơn đã được xác nhận (Ready for shipment)
    PAYMENT_PENDING, // Đã gửi yêu cầu thanh toán (chờ PaymentService)
    PAYMENT_CONFIRMED, // Thanh toán thành công
    SHIPPING, // Muốn SHIP phải PAID + CONFIRMED
    COMPLETED, // Giao hàng thành công
    CREATION_FAILED, // Saga bù trừ đang diễn ra
    CANCELED // Hủy hoàn toàn
}
