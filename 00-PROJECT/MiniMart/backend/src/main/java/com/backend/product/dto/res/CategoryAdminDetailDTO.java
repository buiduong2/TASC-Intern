package com.backend.product.dto.res;

import java.time.LocalDateTime;

import com.backend.product.model.ProductStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryAdminDetailDTO {
    private String imageUrl;
    private Long id;
    private String name;
    private String description;
    private ProductStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
