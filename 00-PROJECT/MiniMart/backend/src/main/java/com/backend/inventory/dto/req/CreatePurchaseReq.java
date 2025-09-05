package com.backend.inventory.dto.req;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePurchaseReq {

    private String supplier;
    private List<PurchaseItemReq> items;

}
