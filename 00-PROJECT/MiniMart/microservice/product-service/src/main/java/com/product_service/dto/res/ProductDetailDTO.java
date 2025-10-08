package com.product_service.dto.res;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ProductDetailDTO {
    private long id;
    private String name;
    private String imageUrl;
    private String description;
    private BigDecimal salePrice;
    private BigDecimal compareAtPrice;
    private int stock;
    private long categoryId;
    private List<TagDTO> tags;

    private List<ProductRelateDTO> relates;

    @Getter
    @Setter
    public static class TagDTO {
        private long id;
        private String name;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductRelateDTO {
        private long id;
        private String name;
        private String imageUrl;

        private BigDecimal salePrice;
        private BigDecimal compareAtPrice;
        private int stock;
    }
}
