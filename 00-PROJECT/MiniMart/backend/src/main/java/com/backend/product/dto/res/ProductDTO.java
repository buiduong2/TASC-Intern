package com.backend.product.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private long id;
    private String name;
    private String imageUrl;
    private Double salePrice;
    private double compareAtPrice;
    private int stock;
}
