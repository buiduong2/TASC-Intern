package com.backend.common.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ProfitReportDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String groupBy;

    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal totalProfit;

    private List<ProfitDataDTO> data; // dùng cho groupBy != NONE

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProfitDataDTO {
        private String label; // "2025-09-01", "2025-09", "2025"
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal profit;
    }
}
