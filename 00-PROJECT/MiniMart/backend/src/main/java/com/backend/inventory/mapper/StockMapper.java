package com.backend.inventory.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.backend.inventory.dto.res.StockAllocationDTO;
import com.backend.inventory.model.StockAllocation;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface StockMapper {

    @Mapping(target = "product", source = "orderItem.product")
    StockAllocationDTO toDTO(StockAllocation stockAllocation);
}
