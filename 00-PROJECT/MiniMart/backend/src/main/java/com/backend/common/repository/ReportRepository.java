package com.backend.common.repository;

import java.math.BigDecimal;
import java.util.List;

import com.backend.common.dto.ProfitReportDTO.ProfitDataDTO;
import com.backend.common.dto.RevenueFilter;
import com.backend.common.dto.RevenueReportDTO.RevenueDataDTO;
import com.backend.common.dto.TopProductDTO;
import com.backend.common.dto.TopProductFilter;

public interface ReportRepository {

    BigDecimal getTotalRevenue(RevenueFilter filter);

    List<RevenueDataDTO> getRevenueGrouping(RevenueFilter filter);

    List<ProfitDataDTO> getTotalProfit(RevenueFilter filter);

    TopProductDTO getTopProduct(TopProductFilter filter);

}
