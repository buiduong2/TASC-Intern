package com.backend.common.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.backend.common.dto.LowStockProductFilter;
import com.backend.common.dto.ProductLowStockDTO;
import com.backend.common.dto.ProfitReportDTO;
import com.backend.common.dto.RevenueFilter;
import com.backend.common.dto.RevenueReportDTO;
import com.backend.common.dto.TopProductDTO;
import com.backend.common.dto.TopProductFilter;
import com.backend.common.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService service;

    // Doanh thu
    // Doanh thu theo ngày (dùng vẽ chart)
    // Doanh thu theo sản phẩm
    // Doanh thu theo category
    // Doanh thu theo khách hàng
    @GetMapping("/revenue")
    public RevenueReportDTO getRevenueSummary(RevenueFilter filter) {
        return service.getRevenue(filter);
    }

    // Lợi nhuận
    // Lợi nhuận tổng trong kỳ
    // Lợi nhuận theo sản phẩm
    // Lợi nhuận theo category
    @GetMapping("/profit")
    public ProfitReportDTO getProfitSummary(RevenueFilter filter) {
        return service.getProfit(filter);
    }

    // Top Sản phẩm lợi nhuận theo Kì
    @GetMapping("/top-products")
    public List<TopProductDTO> getTopProduct(TopProductFilter filter) {
        return service.getTopProduct(filter);
    }

    // Tồn kho
    // Danh sách tồn kho hiện tại
    // Sản phẩm sắp hết hàng
    // Lịch sử nhập – xuất kho
    @GetMapping("/low-stock")
    public List<ProductLowStockDTO> getLowStockProduct(LowStockProductFilter filter,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        return service.getLowStockProduct(filter,PageRequest.of(page, size));
    }

    // Thống kê đơn hàng (tổng số, trạng thái)
    // Số lượng đơn theo trạng thái
    // Doanh thu theo phương thức thanh toán
}
