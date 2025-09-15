package com.backend.order.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.backend.common.config.JpaConfig;
import com.backend.inventory.utils.PurchaseOrderConverter;
import com.backend.order.dto.res.OrderAdminDTO;
import com.backend.order.dto.res.OrderFilter;
import com.backend.order.model.Order_;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Import({ PurchaseOrderConverter.class, JpaConfig.class })
public class CustomOrderRepositoryImplTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void testFindAdminAll() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order_.ID).descending());

        Page<OrderAdminDTO> page = orderRepository.findAdminAll(new OrderFilter(), pageable);

        assertThat(page.getTotalElements()).isEqualTo(orderRepository.count());
        assertThat(page.getSort().isSorted());
        assertThat(page.getNumber()).isEqualTo(0);

        List<OrderAdminDTO> dtos = page.getContent();

        assertThat(dtos).isSortedAccordingTo(Comparator.comparing(OrderAdminDTO::getId, Comparator.reverseOrder()));
        for (OrderAdminDTO orderAdminDTO : dtos) {
            assertThat(orderAdminDTO.getId()).isNotNull();
            assertThat(orderAdminDTO.getCustomerId()).isNotNull();
            assertThat(orderAdminDTO.getPaymentMethod()).isNotNull();
            assertThat(orderAdminDTO.getShippingMethod()).isNotNull();
            assertThat(orderAdminDTO.getProfit()).isNotNull();
            assertThat(orderAdminDTO.getStatus()).isNotNull();
            assertThat(orderAdminDTO.getTotalCost()).isNotNull();
            assertThat(orderAdminDTO.getTotalPrice()).isNotNull();
        }
    }

    @Test
    void testFindAdminAll_withFilterAndSort() {
        OrderFilter filter = new OrderFilter();
        filter.setId(9L);

        filter.setStatus("COMPLETED");

        filter.setPaymentMethod("CARD");

        filter.setShippingMethodId(1L);

        filter.setCustomerId(17L);

        filter.setCreatedFrom(LocalDateTime.parse("2025-09-15T14:00:00"));
        filter.setCreatedTo(LocalDateTime.parse("2025-09-15T15:00:00"));

        filter.setMinTotalPrice(1300.0);
        filter.setMaxTotalPrice(1400.0);

        filter.setMinTotalCost(300.0);
        filter.setMaxTotalCost(400.0);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order_.ID).descending());

        Page<OrderAdminDTO> page = orderRepository.findAdminAll(filter, pageable);

        assertThat(page.getSort().isSorted());
        assertThat(page.getNumberOfElements()).isEqualTo(1);
        assertThat(page.getNumber()).isEqualTo(0);

    }

    @Test
    void testFindAdminAll_withFilterAndSort_withoutId() {
        OrderFilter filter = new OrderFilter();

        filter.setStatus("COMPLETED");

        filter.setPaymentMethod("CARD");

        filter.setCreatedFrom(LocalDateTime.parse("2025-09-15T14:00:00"));
        filter.setCreatedTo(LocalDateTime.parse("2025-09-15T15:00:00"));

        filter.setMinTotalPrice(600.0);
        filter.setMaxTotalPrice(1500.0);

        filter.setMinTotalCost(100.0);
        filter.setMaxTotalCost(400.0);

        Pageable pageable = PageRequest.of(
                0, 10,
                Sort.by(
                        Sort.Order.asc("status"),
                        Sort.Order.desc("totalPrice"),
                        Sort.Order.asc("id")));

        Page<OrderAdminDTO> page = orderRepository.findAdminAll(filter, pageable);

        assertThat(page.getSort().isSorted());
        assertThat(page.getNumber()).isEqualTo(0);

    }
}
