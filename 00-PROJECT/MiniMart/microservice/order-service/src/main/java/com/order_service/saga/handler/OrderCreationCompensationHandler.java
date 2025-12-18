package com.order_service.saga.handler;

import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.finance.payment.PaymentCompensationCompletedEvent;
import com.common_kafka.event.finance.payment.PaymentRefundedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationCompensateCompletedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedCompenstateCompletedEvent;
import com.order_service.event.OrderCancelDomainEvent;
import com.order_service.event.OrderCreationFailedDomainEvent;
import com.order_service.model.Order;

public interface OrderCreationCompensationHandler {
    Order processProductValidationFailedEvent(ProductValidationFailedEvent event);

    Order processInventoryReservedFailed(InventoryReservationFailedEvent event);

    Order processPaymentRefunded(PaymentRefundedEvent event);

    void handleReservationCompensated(InventoryReservedCompenstateCompletedEvent event);

    void handleInventoryAllocationCompensateCompletedEvent(InventoryAllocationCompensateCompletedEvent event);

    void handlePaymentCompensationCompletedEvent(PaymentCompensationCompletedEvent event);

    void handleOrderCreationFailed(OrderCreationFailedDomainEvent event);

    void handleOrderCancel(OrderCancelDomainEvent event);

}
