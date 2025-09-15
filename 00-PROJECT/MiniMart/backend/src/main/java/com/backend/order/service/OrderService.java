package com.backend.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderAdminDTO;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.dto.res.OrderFilter;


public interface OrderService {

    Page<OrderDTO> findPage(Pageable pageable);

    void cancel(Long orderId);

    OrderDTO create(OrderCreateReq req, long userId);

    Page<OrderAdminDTO> findAdminAll(OrderFilter filter, Pageable pageable);
}
