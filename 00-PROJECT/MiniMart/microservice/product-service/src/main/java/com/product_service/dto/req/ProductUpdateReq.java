package com.product_service.dto.req;

import java.math.BigDecimal;
import java.util.HashSet;

import org.hibernate.validator.constraints.Length;

import com.common.validation.EnumValue;
import com.product_service.enums.ProductStatus;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateReq {

    @NotEmpty
    @Length(max = 255, min = 5)
    private String name;

    @NotEmpty
    private String description;

    private BigDecimal salePrice;

    @Positive
    @NotNull
    private BigDecimal compareAtPrice;

    @NotEmpty
    private HashSet<Long> tagIds;

    @NotNull
    @Positive
    private Long categoryId;

    @EnumValue(ProductStatus.class)
    @NotNull
    private String status;

}