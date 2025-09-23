package com.backend.inventory.dto.req;

import java.math.BigDecimal;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePurchaseItemReq {

    @Positive
    private Integer quantity;

    @Positive
    private BigDecimal costPrice;
}
