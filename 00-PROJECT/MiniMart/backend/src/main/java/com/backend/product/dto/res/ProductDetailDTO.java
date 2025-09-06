package com.backend.product.dto.res;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailDTO {
    private long id;
    private String name;
    private String imageUrl;
    private String description;
    private Double salePrice;
    private double compareAtPrice;
    private int stock;
    private List<TagDTO> tags;

    @Getter
    @Setter
    public static class TagDTO {
        private long id;
        private String name;
    }
}
