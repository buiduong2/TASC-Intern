package com.backend.order.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.backend.common.model.Address;
import com.backend.common.repository.AddressRepository;
import com.backend.common.utils.EntityLookupHelper;
import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.mapper.OrderMapper;
import com.backend.order.model.Order;
import com.backend.order.model.OrderStatus;
import com.backend.order.model.ShippingMethod;
import com.backend.order.repository.ShippingMethodRepository;
import com.backend.order.service.OrderService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ShippingMethodRepository shippingMethodRepository;

    private final AddressRepository addressRepository;

    // private final OrderRepo

    @Override
    public Page<OrderDTO> findPage(Pageable pageable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findPage'");
    }

    @Override
    @Transactional
    public OrderDTO create(OrderCreateReq req, long userId) {
        Order entity = new Order();

        entity.setStatus(OrderStatus.PENDING);

        entity.setMessage(req.getMessage());

        // 2. ShippingMethod
        ShippingMethod shippingMethod = EntityLookupHelper.findById(shippingMethodRepository,
                entity.getShippingMethod().getId(), "Shipping");

        entity.setShippingMethod(shippingMethod);

        // ADdress

        if (req.getAddressId() != null) {
            Address address = EntityLookupHelper.findById(addressRepository, req.getAddressId(), "Address");
        } else {
            Address address = new Address();
            // address.setArea(req.getAre);
        }

        return null;
    }

}
