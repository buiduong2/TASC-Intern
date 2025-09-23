package com.backend.common.service.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import com.backend.common.config.JpaConfig;
import com.backend.common.dto.LowStockProductFilter;
import com.backend.common.dto.ProductLowStockDTO;
import com.backend.common.dto.ProfitReportDTO;
import com.backend.common.dto.RevenueFilter;
import com.backend.common.dto.RevenueReportDTO;
import com.backend.common.dto.TopProductDTO;
import com.backend.common.dto.TopProductFilter;
import com.backend.common.repository.impl.ReportRepositoryImpl;
import com.backend.common.service.ReportService;
import com.backend.inventory.utils.PurchaseOrderConverter;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
@Import({ PurchaseOrderConverter.class, JpaConfig.class, ReportRepositoryImpl.class, ReportServiceImpl.class })
public class ReportServiceImplTest {

    @Autowired
    private ReportService reportService;

    @Test
    void getRevenue_nullGroupBy_shouldReturnTotalRevenue() {
        RevenueFilter filter = new RevenueFilter();
        filter.setEndDate(LocalDateTime.now());
        filter.setStartDate(LocalDateTime.now().minusMonths(1));
        filter.setGroupBy("NONE");

        RevenueReportDTO dto = reportService.getRevenue(filter);

        assertThat(dto.getTotalRevenue()).isNotNull();

    }

    @Test
    void getRevenue_groupByDay_shouldReturnDayData() {
        RevenueFilter filter = new RevenueFilter();
        filter.setEndDate(LocalDateTime.now());
        filter.setStartDate(LocalDateTime.now().minusMonths(1));
        filter.setGroupBy("DAY");

        RevenueReportDTO dto = reportService.getRevenue(filter);

        assertThat(dto.getTotalRevenue()).isNull();
        assertThat(dto.getData()).isNotNull();
    }

    @Test
    void testGetRevenue_groupByMonth_shouldReturnMonthData() {
        RevenueFilter filter = new RevenueFilter();
        filter.setEndDate(LocalDateTime.now());
        filter.setStartDate(LocalDateTime.now().minusMonths(1));
        filter.setGroupBy("MONTH");

        RevenueReportDTO dto = reportService.getRevenue(filter);

        assertThat(dto.getTotalRevenue()).isNull();
        assertThat(dto.getData()).isNotNull();
    }

    @Test
    void testGetRevenue_groupByYear_shouldReturnYearData() {
        RevenueFilter filter = new RevenueFilter();
        filter.setEndDate(LocalDateTime.now());
        filter.setStartDate(LocalDateTime.now().minusMonths(1));
        filter.setGroupBy("MONTH");

        RevenueReportDTO dto = reportService.getRevenue(filter);

        assertThat(dto.getTotalRevenue()).isNull();
        assertThat(dto.getData()).isNotNull();
    }

    @Test
    void testGetProfit() {
        RevenueFilter filter = new RevenueFilter();
        filter.setEndDate(LocalDateTime.now());
        filter.setStartDate(LocalDateTime.now().minusMonths(1));
        filter.setGroupBy("NONE");

        ProfitReportDTO dto = reportService.getProfit(filter);

        assertThat(dto.getTotalCost()).isNotNull();
        assertThat(dto.getTotalProfit()).isNotNull();
        assertThat(dto.getTotalRevenue()).isNotNull();

    }

    @Test
    void getProfit_byDay_shouldValid() {
        RevenueFilter filter = new RevenueFilter();
        filter.setEndDate(LocalDateTime.now());
        filter.setStartDate(LocalDateTime.now().minusMonths(1));
        filter.setGroupBy("DAY");

        reportService.getProfit(filter);

    }

    @Test
    void getProfit_byMonth_shouldValid() {
        RevenueFilter filter = new RevenueFilter();
        filter.setEndDate(LocalDateTime.now());
        filter.setStartDate(LocalDateTime.now().minusMonths(1));
        filter.setGroupBy("MONTH");

        reportService.getProfit(filter);

    }

    @Test
    void getProfit_byYear_shouldValid() {
        RevenueFilter filter = new RevenueFilter();
        filter.setEndDate(LocalDateTime.now());
        filter.setStartDate(LocalDateTime.now().minusMonths(1));
        filter.setGroupBy("YEAR");

        reportService.getProfit(filter);

    }

    @Test
    void testGetTopProduct_byDay_shouldValid() {
        TopProductFilter filter = new TopProductFilter();
        filter.setPeriod("YEAR");
        filter.setMetric("quantity");

        List<TopProductDTO> dtos = reportService.getTopProduct(filter);

        dtos.forEach(System.out::println);

    }

    @Test
    void testGetLowStockProduct() {
        LowStockProductFilter filter = new LowStockProductFilter();
        filter.setThreshold(10);

        List<ProductLowStockDTO> dtos = reportService.getLowStockProduct(filter, PageRequest.of(0, 10));

        dtos.forEach(System.out::println);
    }
}
