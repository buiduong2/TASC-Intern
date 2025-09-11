package com.backend.product.dto.res;

import java.time.LocalDateTime;

import com.backend.product.model.ProductStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryAdminDTO {
    private Long id;
    private String imageUrl;
    private String name;
    private ProductStatus status;
    private long productCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
