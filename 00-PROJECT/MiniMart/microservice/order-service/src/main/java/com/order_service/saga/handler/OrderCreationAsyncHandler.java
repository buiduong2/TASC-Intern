package com.order_service.saga.handler;

import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.order_service.event.OrderPreparedDomainEvent;
import com.order_service.model.Order;

public interface OrderCreationAsyncHandler {

    void handleOrderPrepared(OrderPreparedDomainEvent event);

    Order processProductValidationPassedEvent(ProductValidationPassedEvent event);

    Order processInventoryReservedConfirmed(InventoryReservedConfirmedEvent event);

    Order processPaymentRecordCreated(PaymentRecordPreparedEvent event);

    Order processInventoryAllocationConfirmed(InventoryAllocationConfirmedEvent event);

}
