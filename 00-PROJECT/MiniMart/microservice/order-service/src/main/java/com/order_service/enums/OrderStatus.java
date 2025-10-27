package com.order_service.enums;

import java.util.Set;

import com.order_service.model.Order;

public enum OrderStatus {
    VALIDATING, // Đang chờ Product + Stock xác thực
    CONFIRMED, // Đơn đã được xác nhận (Ready for shipment
    SHIPPING, // Muốn SHIP phải PAID + CONFIRMED
    COMPLETED, // Giao hàng thành công
    CREATION_FAILED, // Saga bù trừ đang diễn ra
    AWAITING_CANCEL,
    CANCELING,
    CANCELED // Hủy hoàn toàn
    ;

    private final static Set<OrderStatus> endedStatuses = Set.of(
            OrderStatus.COMPLETED,
            OrderStatus.CANCELED,
            OrderStatus.CREATION_FAILED);

    public static boolean isOrderEnded(Order order) {
        return endedStatuses.contains(order.getStatus());
    }

    public static boolean isCancellingOrAwaiting(Order order) {
        return order.getStatus() == CANCELING ||
                order.getStatus() == AWAITING_CANCEL;
    }

    private final static Set<OrderStatus> cancelableStatus = Set.of(
            OrderStatus.VALIDATING,
            OrderStatus.CONFIRMED);

    public static boolean isEligibleForNewCancelRequest(Order order) {
        return cancelableStatus.contains(order.getStatus());
    }

}
