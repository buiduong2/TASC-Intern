package com.inventory_service.dto.res;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.inventory_service.enums.PurchaseStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseSummaryDTO {
    private long id;
    private String supplier;
    private int totalQuantity;
    private BigDecimal totalCostPrice;

    private PurchaseStatus status;
    private LocalDateTime createdAt;
}
