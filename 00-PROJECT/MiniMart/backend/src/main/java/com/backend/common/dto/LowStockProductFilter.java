package com.backend.common.dto;

import com.backend.common.validation.EnumValue;
import com.backend.product.model.ProductStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LowStockProductFilter {

    @Positive
    @NotNull
    private Integer threshold; // ngưỡng tồn kho (ví dụ < 10)

    private Long categoryId; // lọc theo category

    @EnumValue(enumClass = ProductStatus.class)
    private String status; // Lọc theo status
}
