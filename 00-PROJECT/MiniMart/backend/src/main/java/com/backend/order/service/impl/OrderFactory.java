package com.backend.order.service.impl;

import org.springframework.stereotype.Service;

import com.backend.common.exception.ResourceNotFoundException;
import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.mapper.OrderMapper;
import com.backend.order.model.Order;
import com.backend.order.model.OrderStatus;
import com.backend.order.model.ShippingMethod;
import com.backend.order.repository.ShippingMethodRepository;
import com.backend.user.model.Customer;
import com.backend.user.repository.CustomerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderFactory {

    private final OrderMapper mapper;

    private final CustomerRepository customerRepository;

    private final ShippingMethodRepository shippingMethodRepository;

    public Order create(OrderCreateReq req, long userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found for userId=" + userId));

        ShippingMethod shippingMethod = shippingMethodRepository.getReferenceById(req.getShippingMethodId());

        Order order = new Order();
        order.setMessage(req.getMessage());
        order.setStatus(OrderStatus.PENDING);
        order.setCustomer(customer);
        order.setAddress(mapper.toAddress(req.getAddress()));
        order.setShippingMethod(shippingMethod);

        return order;
    }
}
