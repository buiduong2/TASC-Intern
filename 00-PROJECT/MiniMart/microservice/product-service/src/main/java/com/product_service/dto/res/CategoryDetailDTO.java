package com.product_service.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategoryDetailDTO {
    private long id;
    private String name;
    private String imageUrl;
    private String description;
}
