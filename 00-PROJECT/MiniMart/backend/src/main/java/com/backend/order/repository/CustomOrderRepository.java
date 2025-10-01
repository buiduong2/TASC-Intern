package com.backend.order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.order.dto.res.OrderAdminDTO;
import com.backend.order.dto.res.OrderFilter;

public interface CustomOrderRepository {

    Page<OrderAdminDTO> findAdminAll(OrderFilter filter, Pageable pageable);


}
