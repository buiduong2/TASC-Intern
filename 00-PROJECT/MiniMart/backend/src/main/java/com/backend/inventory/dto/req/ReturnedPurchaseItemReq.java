package com.backend.inventory.dto.req;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReturnedPurchaseItemReq {

    @Positive
    @NotNull
    private Integer returnQuantity;
}
