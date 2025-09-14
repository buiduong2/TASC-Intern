package com.backend.product.dto.req;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagFilter {

    private String name;
    private LocalDateTime createdFrom;
    private LocalDateTime updatedFrom;

    @Min(0)
    private Long minProductCount;
    
    @Min(0)
    private Long maxProductCount;
}