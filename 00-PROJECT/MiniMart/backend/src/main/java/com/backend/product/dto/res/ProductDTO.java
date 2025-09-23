package com.backend.product.dto.res;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private long id;
    private String name;
    private String imageUrl;
    private BigDecimal salePrice;
    private BigDecimal compareAtPrice;
    private int stock;

    @Override
    public String toString() {
        return "ProductDTO [id=" + id + ", name=" + name + ", imageUrl=" + imageUrl + ", salePrice=" + salePrice
                + ", compareAtPrice=" + compareAtPrice + ", stock=" + stock + "]";
    }

}
