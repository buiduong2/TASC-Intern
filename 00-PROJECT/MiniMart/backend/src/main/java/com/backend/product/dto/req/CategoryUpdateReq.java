package com.backend.product.dto.req;

import com.backend.common.validation.EnumValue;
import com.backend.product.model.ProductStatus;

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
    @EnumValue(enumClass = ProductStatus.class)
    private ProductStatus status;
}
