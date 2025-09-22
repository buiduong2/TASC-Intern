package com.backend.common.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.backend.common.dto.LowStockProductFilter;
import com.backend.common.dto.ProductLowStockDTO;
import com.backend.common.dto.ProfitReportDTO;
import com.backend.common.dto.ProfitReportDTO.ProfitDataDTO;
import com.backend.common.dto.RevenueFilter;
import com.backend.common.dto.RevenueReportDTO;
import com.backend.common.dto.RevenueReportDTO.RevenueDataDTO;
import com.backend.common.dto.TopProductDTO;
import com.backend.common.dto.TopProductFilter;
import com.backend.common.repository.ReportRepository;
import com.backend.common.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository repository;

    @Override
    public RevenueReportDTO getRevenue(RevenueFilter filter) {
        RevenueReportDTO dto = new RevenueReportDTO();

        dto.setStartDate(filter.getStartDate());
        dto.setEndDate(filter.getEndDate());
        if (filter.getGroupBy().equals("NONE")) {
            BigDecimal totalRevenue = repository.getTotalRevenue(filter);
            dto.setTotalRevenue(totalRevenue);
        } else {
            List<RevenueDataDTO> datas = repository.getRevenueGrouping(filter);
            dto.setData(datas);
        }
        return dto;
    }

    @Override
    public ProfitReportDTO getProfit(RevenueFilter filter) {
        ProfitReportDTO dto = new ProfitReportDTO();

        dto.setStartDate(filter.getStartDate());
        dto.setEndDate(filter.getEndDate());
        if (filter.getGroupBy().equals("NONE")) {
            ProfitDataDTO totalRevenue = repository.getTotalProfit(filter).get(0);
            dto.setTotalCost(totalRevenue.getCost());
            dto.setTotalProfit(totalRevenue.getProfit());
            dto.setTotalRevenue(totalRevenue.getRevenue());
        } else {
            List<ProfitDataDTO> datas = repository.getTotalProfit(filter);
            dto.setData(datas);
        }
        return dto;
    }

    @Override
    public List<TopProductDTO> getTopProduct(TopProductFilter filter) {
        return repository.getTopProduct(filter);
    }

    @Override
    public List<ProductLowStockDTO> getLowStockProduct(LowStockProductFilter filter, PageRequest pageRequest) {
        return repository.getLowStockProduct(filter, pageRequest);
    }

}
