package com.backend.common.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RevenueFilter {

    @Past
    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @Pattern(regexp = "NONE|DAY|MONTH|YEAR")
    @NotNull
    private String groupBy;

    private Long productId;
    private Long categoryId;
    private Long customerId;

}
