package com.order_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.req.OrderFilter;
import com.order_service.dto.req.OrderUpdateReq;
import com.order_service.dto.res.OrderAdminDTO;
import com.order_service.dto.res.OrderDTO;
import com.order_service.dto.res.OrderDetailDTO;

public interface OrderService {

    Page<OrderDTO> findPage(Pageable pageable, Long id);

    OrderDetailDTO findByIdAndUserId(long id, Long id2);

    OrderDTO create(OrderCreateReq req, Long id);

    OrderDTO createFromCart(OrderCreateReq req, Long id);

    void cancel(Long orderId, Long id);

    Page<OrderAdminDTO> findAdminAll(OrderFilter filter, Pageable pageable);

    OrderDTO cancelAdmin(long id);

    OrderDTO updateStatus(long id, OrderUpdateReq req);

}
