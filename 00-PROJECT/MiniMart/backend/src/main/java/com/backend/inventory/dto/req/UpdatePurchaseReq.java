package com.backend.inventory.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePurchaseReq {

    @NotBlank
    private String supplier;
}
