package com.backend.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.backend.order.dto.res.OrderDTO;

public interface OrderService extends OrderCreateService, OrderCancelService {

    Page<OrderDTO> findPage(Pageable pageable);

}
