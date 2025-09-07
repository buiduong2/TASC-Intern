package com.backend.inventory.dto.req;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePurchaseReq {

    @NotBlank
    private String supplier;

    @NotEmpty
    @Valid
    private List<PurchaseItemReq> items;

    

}
