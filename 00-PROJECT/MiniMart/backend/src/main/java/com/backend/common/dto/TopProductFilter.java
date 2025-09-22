package com.backend.common.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TopProductFilter {

    @Pattern(regexp = "quantity|revenue|profit")
    @NotNull
    private String metric; // "quantity" | "revenue" | "profit"
    @Pattern(regexp = "DAY|WEEK|MONTH|YEAR|RANGE")
    @NotNull
    private String period; // "DAY" | "WEEK" | "MONTH" | "YEAR" | "RANGE"

    @Past
    private LocalDateTime startDate; // nếu period = RANGE thì bắt buộc

    private LocalDateTime endDate;
    @Positive
    private int limit = 10; // số sản phẩm top N

    @Positive
    private Long categoryId; // optional: lọc theo category
}