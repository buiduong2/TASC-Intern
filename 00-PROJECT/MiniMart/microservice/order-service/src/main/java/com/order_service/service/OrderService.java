package com.order_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.common_kafka.event.catalog.product.ProductValidationFailedEvent;
import com.common_kafka.event.catalog.product.ProductValidationPassedEvent;
import com.common_kafka.event.finance.payment.PaymentRecordPreparedEvent;
import com.common_kafka.event.supply.inventory.InventoryAllocationConfirmedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.req.OrderFilter;
import com.order_service.dto.req.OrderUpdateReq;
import com.order_service.dto.res.OrderAdminDTO;
import com.order_service.dto.res.OrderDTO;
import com.order_service.dto.res.OrderDetailDTO;
import com.order_service.model.Order;

public interface OrderService {

    Page<OrderDTO> findPage(Pageable pageable, Long id);

    OrderDetailDTO findByIdAndUserId(long id, Long userId);

    OrderDTO create(OrderCreateReq req, Long userId);

    OrderDTO createFromCart(OrderCreateReq req, Long userId);

    void cancel(Long orderId, Long userId);

    Page<OrderAdminDTO> findAdminAll(OrderFilter filter, Pageable pageable);

    OrderDTO cancelAdmin(long id);

    OrderDTO updateStatus(long id, OrderUpdateReq req);

    Order processProductValidationPassedEvent(ProductValidationPassedEvent event);

    Order processProductValidationFailed(ProductValidationFailedEvent event);

    Order processInventoryReservedConfirmed(InventoryReservedConfirmedEvent event);

    Order processInventoryReservationFailed(InventoryReservationFailedEvent event);

    Order processPaymentRecordCreated(PaymentRecordPreparedEvent event);

    Order processInventoryAllocationConfirmed(InventoryAllocationConfirmedEvent event);

}
