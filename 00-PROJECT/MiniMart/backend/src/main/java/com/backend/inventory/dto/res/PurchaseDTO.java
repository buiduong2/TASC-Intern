package com.backend.inventory.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDTO {
    private long id;
    private LocalDateTime createdAt; 
    private String supplier;
    private int totalQuantity;
    private BigDecimal totalCostPrice;
}
