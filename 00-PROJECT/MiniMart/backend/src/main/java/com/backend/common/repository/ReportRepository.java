package com.backend.common.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.backend.common.dto.LowStockProductFilter;
import com.backend.common.dto.ProductLowStockDTO;
import com.backend.common.dto.ProfitReportDTO.ProfitDataDTO;
import com.backend.common.dto.RevenueFilter;
import com.backend.common.dto.RevenueReportDTO.RevenueDataDTO;
import com.backend.common.dto.TopProductDTO;
import com.backend.common.dto.TopProductFilter;

public interface ReportRepository {

    BigDecimal getTotalRevenue(RevenueFilter filter);

    List<RevenueDataDTO> getRevenueGrouping(RevenueFilter filter);

    List<ProfitDataDTO> getTotalProfit(RevenueFilter filter);

    List<TopProductDTO> getTopProduct(TopProductFilter filter);

    List<ProductLowStockDTO> getLowStockProduct(LowStockProductFilter filter, PageRequest pageRequest);

}
