package com.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.common_kafka.event.sales.order.OrderStockReservationRequestedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;

@FeignClient("inventory-service")
public interface InventoryClient {

    @PostMapping("/v1/internal/allocations/order-reserve")
    InventoryReservedConfirmedEvent reservateOrder(@RequestBody OrderStockReservationRequestedEvent event);
}
