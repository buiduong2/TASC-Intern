package com.backend.common.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TopProductDTO {
    private Long productId;
    private String productName;
    private String imageUrl;

    private Long totalSold; // tổng số lượng bán
    private BigDecimal totalRevenue; // tổng doanh thu
    private BigDecimal totalProfit; // tổng lợi nhuận
}