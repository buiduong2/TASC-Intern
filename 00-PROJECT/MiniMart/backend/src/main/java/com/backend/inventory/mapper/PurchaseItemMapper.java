package com.backend.inventory.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.backend.inventory.dto.req.PurchaseItemReq;
import com.backend.inventory.dto.res.PurchaseItemDTO;
import com.backend.inventory.model.PurchaseItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)

public interface PurchaseItemMapper {

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "remainingQuantity", source = "quantity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    PurchaseItem toEntity(PurchaseItemReq req);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "purchaseId", source = "purchase.id")
    PurchaseItemDTO toDTO(PurchaseItem purchase);

}
