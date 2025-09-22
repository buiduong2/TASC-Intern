package com.backend.common.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import com.backend.common.dto.LowStockProductFilter;
import com.backend.common.dto.ProductLowStockDTO;
import com.backend.common.dto.ProfitReportDTO;
import com.backend.common.dto.RevenueFilter;
import com.backend.common.dto.RevenueReportDTO;
import com.backend.common.dto.TopProductDTO;
import com.backend.common.dto.TopProductFilter;

public interface ReportService {

    RevenueReportDTO getRevenue(RevenueFilter filter);

    ProfitReportDTO getProfit(RevenueFilter filter);

    List<TopProductDTO> getTopProduct(TopProductFilter filter);

    List<ProductLowStockDTO> getLowStockProduct(LowStockProductFilter filter, PageRequest of);

}
