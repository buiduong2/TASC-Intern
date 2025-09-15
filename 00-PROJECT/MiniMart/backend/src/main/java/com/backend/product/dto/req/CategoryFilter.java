package com.backend.product.dto.req;

import java.time.LocalDateTime;

import com.backend.common.validation.EnumValue;
import com.backend.product.model.ProductStatus;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
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
    
    @Positive
    private Long minProductCount;

    @Positive
    private Long maxProductCount;
}