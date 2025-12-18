package com.inventory_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.common_kafka.event.sales.order.OrderStockReservationRequestedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservationFailedEvent;
import com.common_kafka.event.supply.inventory.InventoryReservedConfirmedEvent;
import com.common_kafka.exception.saga.IdempotentEventException;
import com.common_kafka.exception.saga.LogicViolationException;
import com.inventory_service.dto.res.ReservateStockResult;
import com.inventory_service.saga.StockSagaManager;
import com.inventory_service.service.StockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/internal/allocations")
@RequiredArgsConstructor
public class InternalStockAllocationController {

    private final StockService stockService;

    private final StockSagaManager stockSagaManager;

    @PostMapping("/order-reserve")
    public ResponseEntity<?> reservateOrder(@RequestBody OrderStockReservationRequestedEvent event) {
        try {
            ReservateStockResult result = stockService.processOrderStockReservationRequested(event);

            InventoryReservedConfirmedEvent res = stockSagaManager.createInventoryReservedConfirmEvent(event,
                    result.getLogs(), result.getAllocation());

            return ResponseEntity.ok(res);
        } catch (LogicViolationException e) {
            InventoryReservationFailedEvent failedEvent = stockSagaManager.createInventoryReservedFailedEvent(event,
                    e.getMessage());
            return ResponseEntity.badRequest().body(failedEvent);
        } catch (IdempotentEventException e) {
            return ResponseEntity.internalServerError().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
