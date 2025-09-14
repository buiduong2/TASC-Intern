package com.backend.product.dto.req;

import java.time.LocalDateTime;

import com.backend.common.validation.EnumValue;
import com.backend.product.model.ProductStatus;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryFilter {

    private String name;

    @EnumValue(enumClass = ProductStatus.class)
    private String status;

    @Past
    private LocalDateTime createdFrom;

    @Past
    private LocalDateTime updatedFrom;

    @Min(1)
    private Long minProductCount;

    @Min(1)
    private Long maxProductCount;
}