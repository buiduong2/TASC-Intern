package com.order_service.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.common.mapper.ToEntity;
import com.order_service.dto.req.AddressUpdateReq;
import com.order_service.dto.req.OrderCreateReq;
import com.order_service.dto.req.OrderItemCreateReq;
import com.order_service.dto.res.OrderDTO;
import com.order_service.model.Address;
import com.order_service.model.Order;
import com.order_service.model.OrderItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface OrderMapper {

    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    Address toAddress(AddressUpdateReq dto);

    @Mapping(target = "avgCostPrice", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    @Mapping(target = "order", ignore = true)
    @Mapping(target = "id", ignore = true)
    OrderItem toItem(OrderItemCreateReq req);

    @ToEntity
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "shippingMethod", ignore = true)
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "paymentStatus", ignore = true)
    Order toEntity(OrderCreateReq req);

    @Mapping(target = "shippingMethod", source = "shippingMethod.name")
    @Mapping(target = "shippingCost", source = "shippingMethod.cost")
    OrderDTO toClientSummaryDTO(Order order);
}