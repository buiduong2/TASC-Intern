package com.backend.common.service;

import com.backend.common.dto.ProfitReportDTO;
import com.backend.common.dto.RevenueFilter;
import com.backend.common.dto.RevenueReportDTO;
import com.backend.common.dto.TopProductDTO;
import com.backend.common.dto.TopProductFilter;

public interface ReportService {

    RevenueReportDTO getRevenue(RevenueFilter filter);

    ProfitReportDTO getProfit(RevenueFilter filter);

    TopProductDTO getTopProduct(TopProductFilter filter);

}
