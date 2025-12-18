package com.order_service.service;

import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.finance.payment.PaymentPaidEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.finance.payment.PaymentRefundedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.order_service.dto.req.OrderCreateReq;
import com.order_service.model.Order;

public interface OrderCoreService {

    Order processProductValidationPassedEvent(ProductValidationPassedEvent event);

    Order processInventoryReservedConfirmed(InventoryReservedConfirmedEvent event);

    Order processPaymentRecordCreated(PaymentRecordPreparedEvent event);

    Order processInventoryAllocationConfirmed(InventoryAllocationConfirmedEvent event);

    Order processCanceled(long orderId, long userId);

    void processPaymentPaidEvent(PaymentPaidEvent event);

    Order processOrderCreationCompensated(long orderId, long userId);

    void processPaymentRefunded(PaymentRefundedEvent event);

    Order createRaw(OrderCreateReq req, long userId);

    Order getOrderSummaryDTOById(long orderId, long userId);
}
