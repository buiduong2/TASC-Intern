package com.backend.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.backend.order.dto.res.PaymentTransactionDTO;
import com.backend.order.model.PaymentTransaction;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentMapper {

    @Mapping(target = "paymentUrl", ignore = true)
    @Mapping(target = "paymentId", source = "payment.id")
    PaymentTransactionDTO toTransactionDTO(PaymentTransaction transaction);
}
