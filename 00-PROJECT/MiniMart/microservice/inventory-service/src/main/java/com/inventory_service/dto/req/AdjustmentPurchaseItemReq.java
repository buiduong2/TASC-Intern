package com.inventory_service.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdjustmentPurchaseItemReq {
    private int deltaRemainingQuantity;
}
