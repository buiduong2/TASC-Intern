package com.inventory_service.dto.res;

import java.util.List;

import com.inventory_service.model.Allocation;
import com.inventory_service.model.OrderReservationLog;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor(staticName = "of")
public class ReservateStockResult {
    private final List<OrderReservationLog> logs;
    private final Allocation allocation;
}
