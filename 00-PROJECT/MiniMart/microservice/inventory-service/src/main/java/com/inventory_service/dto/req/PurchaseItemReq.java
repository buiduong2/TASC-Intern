package com.inventory_service.dto.req;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseItemReq {
    @NotNull
    @Positive
    private Long productId;

    @Positive
    private int quantity;

    @Positive
    @NotNull
    private BigDecimal costPrice;
}