package com.backend.inventory.dto.res;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PurchaseDTO {
    private long id;
    private LocalDateTime createdAt; 
    private String supplier;
    private int totalQuantity;
    private double totalCostPrice;
}
