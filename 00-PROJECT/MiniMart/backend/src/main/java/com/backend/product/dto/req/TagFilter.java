package com.backend.product.dto.req;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagFilter {

    private String name;

    @Past
    private LocalDateTime createdFrom;

    @Past
    private LocalDateTime updatedFrom;

    @Positive
    private Long minProductCount;

    @Positive
    private Long maxProductCount;
}