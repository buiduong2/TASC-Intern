package com.backend.inventory.dto.event;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PurchaseDeleteEvent {
    private Long purchaseId;
    private List<Long> productIds;
}
