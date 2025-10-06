package com.product_service.dto.res;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class ProductSummaryDTO {
    private Long id;
    private String name;
    private String imageUrl;
    private BigDecimal salePrice;
    private BigDecimal compareAtPrice;
    private int stock;
}
