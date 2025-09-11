package com.backend.product.dto.req;

import java.util.HashSet;

import org.hibernate.validator.constraints.Length;

import com.backend.common.validation.EnumValue;
import com.backend.product.model.ProductStatus;

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

    private Double salePrice;

    @Positive
    @NotNull
    private Double compareAtPrice;

    @NotEmpty
    private HashSet<Long> tagIds;

    @NotNull
    @Positive
    private Long categoryId;

    @EnumValue(enumClass = ProductStatus.class)
    private String status;

}