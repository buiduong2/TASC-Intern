package com.inventory_service.dto.req;

import com.common.validation.EnumValue;
import com.inventory_service.enums.PurchaseStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateStatusPurchaseReq {

    @EnumValue(value = PurchaseStatus.class, exclude = "DRAFT")
    @NotNull
    private String status;
}
