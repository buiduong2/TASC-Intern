package com.backend.product.dto.res;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryDTO {
    private long id;
    private String name;
    private String imageUrl;
    private String description;
}
