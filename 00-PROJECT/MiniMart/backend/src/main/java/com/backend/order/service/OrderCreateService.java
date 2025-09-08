package com.backend.order.service;

import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderDTO;

public interface OrderCreateService {

    OrderDTO create(OrderCreateReq req, long userId);
}
