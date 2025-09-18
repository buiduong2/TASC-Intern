package com.backend.inventory.dto.res;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseItemDTO {
    private Long id;
    private int quantity;
    private int remainingQuantity;
    private double costPrice;
    private long purchaseId;
    private long productId;
    private LocalDateTime createdAt;
}
