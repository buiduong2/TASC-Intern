package com.backend.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.model.OrderAddress;

@Mapper
public interface OrderMapper {

    @Mapping(target = "profile", source = ".")
    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "id", ignore = true)
    OrderAddress toAddress(OrderCreateReq.AddressDTO dto);
}
