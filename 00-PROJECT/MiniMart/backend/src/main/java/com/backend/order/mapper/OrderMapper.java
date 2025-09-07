package com.backend.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.backend.common.utils.ToEntity;
import com.backend.order.dto.event.OrderCreatedEvent;
import com.backend.order.dto.event.OrderCreatedEvent.OrderItemEvent;
import com.backend.order.dto.req.OrderCreateReq;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.model.Order;
import com.backend.order.model.OrderAddress;
import com.backend.order.model.OrderItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "profile", source = ".")
    @ToEntity
    OrderAddress toAddress(OrderCreateReq.AddressDTO dto);

    OrderCreatedEvent toCreatedEvent(Order order);

    @Mapping(target = "productId", source = "product.id")
    OrderItemEvent toOrderItem(OrderItem orderItem);

    @Mapping(target = ".", source = "audit")
    @Mapping(target = "paymentMethod", source = "payment.name")
    @Mapping(target = "shippingMethod", source = "shippingMethod.name")
    @Mapping(target = "shippingCost", source = "shippingMethod.cost")
    OrderDTO toDTO(Order order);
}
