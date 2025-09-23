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
public class RevenueReportDTO {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

  
    private String groupBy; // NONE, DAY, MONTH, YEAR

    private BigDecimal totalRevenue; // dùng khi groupBy = NONE
    private List<RevenueDataDTO> data; // dùng khi groupBy != NONE

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RevenueDataDTO {
        private String label; // "2025-09-01", "2025-09", "2025"
        private BigDecimal revenue;
    }
}
