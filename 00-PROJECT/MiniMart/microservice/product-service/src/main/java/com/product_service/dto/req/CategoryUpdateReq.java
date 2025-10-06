package com.product_service.dto.req;

import com.common.validation.EnumValue;
import com.product_service.enums.ProductStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateReq {

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    @NotNull
    @EnumValue(ProductStatus.class)
    private ProductStatus status;
}
