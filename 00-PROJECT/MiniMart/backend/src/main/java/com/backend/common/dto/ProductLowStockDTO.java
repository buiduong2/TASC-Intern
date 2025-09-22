package com.backend.common.dto;

import com.backend.product.model.ProductStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class ProductLowStockDTO {
    private Long id;

    private String name;

    private CategoryDTO category;

    private int stock;

    private ProductStatus status;

    private Double salePrice;

    private double compareAtPrice;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class CategoryDTO {
        private Long id;
        private String name;
    }

}
