package com.order_service.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.order_service.dto.res.PaymentTransactionDTO;
import com.order_service.model.PaymentTransaction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PaymentMapper {

    @Mapping(target = "paymentUrl", ignore = true)
    @Mapping(target = "paymentId", source = "payment.id")
    PaymentTransactionDTO toTransactionDTO(PaymentTransaction transaction);
}
