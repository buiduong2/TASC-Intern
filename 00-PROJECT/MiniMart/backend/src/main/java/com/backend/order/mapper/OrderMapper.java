package com.backend.order.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.backend.common.utils.ToEntity;
import com.backend.order.dto.event.OrderCreatedEvent;
import com.backend.order.dto.event.OrderCreatedEvent.OrderItemEvent;
import com.backend.order.dto.req.OrderAddressReq;
import com.backend.order.dto.res.OrderDTO;
import com.backend.order.dto.res.OrderDetailDTO;
import com.backend.order.dto.res.OrderDetailDTO.OrderItemDTO;
import com.backend.order.model.Order;
import com.backend.order.model.OrderAddress;
import com.backend.order.model.OrderItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    @Mapping(target = "profile", source = ".")
    @ToEntity
    OrderAddress toAddress(OrderAddressReq dto);

    @InheritConfiguration(name = "toAddress")
    void updateAddress(@MappingTarget OrderAddress target, OrderAddressReq req);

    OrderCreatedEvent toCreatedEvent(Order order);

    @Mapping(target = "productId", source = "product.id")
    OrderItemEvent toOrderItem(OrderItem orderItem);

    @Mapping(target = ".", source = "audit")
    @Mapping(target = "paymentMethod", source = "payment.name")
    @Mapping(target = "paymentId", source = "payment.id")
    @Mapping(target = "shippingMethod", source = "shippingMethod.name")
    @Mapping(target = "shippingCost", source = "shippingMethod.cost")
    @Mapping(target = "paymentStatus", source = "payment.status")
    OrderDTO toDTO(Order order);

    @Mapping(target = "createdAt", source = "audit.createdAt")
    @Mapping(target = "updatedAt", source = "audit.updatedAt")
    OrderDetailDTO toDetailDTO(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "productImageUrl", source = "product.image.url")
    OrderItemDTO toDetailItemDTO(OrderItem orderItem);
}
