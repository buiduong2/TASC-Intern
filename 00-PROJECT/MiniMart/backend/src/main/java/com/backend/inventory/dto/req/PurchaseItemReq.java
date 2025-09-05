package com.backend.inventory.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PurchaseItemReq {
    private long productId;
    private int quantity;
    private double costPrice;

}