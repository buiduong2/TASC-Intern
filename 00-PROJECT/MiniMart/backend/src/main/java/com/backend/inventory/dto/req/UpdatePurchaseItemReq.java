package com.backend.inventory.dto.req;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePurchaseItemReq {

    @Positive
    private Integer quantity;

    @Positive
    private Double costPrice;
}
