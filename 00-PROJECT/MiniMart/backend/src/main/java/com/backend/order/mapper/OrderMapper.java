package com.backend.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.backend.common.model.Address;
import com.backend.order.dto.req.OrderCreateReq;

@Mapper
public interface OrderMapper {

    @Mapping(target = "profile.firstName", source = "firstName")
    @Mapping(target = "profile.lastName", source = "lastName")
    @Mapping(target = "profile.email", source = "email")
    @Mapping(target = "profile.phone", source = "phone")
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "audit", ignore = true)
    Address toAddress(OrderCreateReq.AddressDTO dto);
}
