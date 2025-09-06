package com.backend.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderDTO;

public interface OrderService {

    Page<OrderDTO> findPage(Pageable pageable);

    OrderDTO create(OrderCreateReq req, long userId);

}
