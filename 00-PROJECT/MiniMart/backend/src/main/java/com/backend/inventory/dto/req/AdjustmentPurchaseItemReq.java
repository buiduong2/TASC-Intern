package com.backend.inventory.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjustmentPurchaseItemReq {

    private int deltaRemainingQuantity;
}