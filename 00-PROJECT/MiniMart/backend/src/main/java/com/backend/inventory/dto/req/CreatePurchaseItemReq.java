package com.backend.inventory.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePurchaseItemReq {
    private int quantity;
    private int remainingQuantity;
    private double costPrice;
    private long purchaseId;
    private long productId;
}
