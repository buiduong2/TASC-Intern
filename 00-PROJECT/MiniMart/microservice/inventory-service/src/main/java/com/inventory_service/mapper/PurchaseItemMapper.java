package com.inventory_service.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.inventory_service.dto.req.PurchaseItemReq;
import com.inventory_service.dto.res.PurchaseItemDTO;
import com.inventory_service.model.PurchaseItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PurchaseItemMapper {
    @Mapping(target = "remainingQuantity", source = "quantity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchase", ignore = true)
    @Mapping(target = "allocationItems", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    PurchaseItem toEntity(PurchaseItemReq itemReqs);

    List<PurchaseItem> toEntities(List<PurchaseItemReq> reqs);

    @Mapping(target = "purchaseId", source = "purchase.id")
    PurchaseItemDTO toDTO(PurchaseItem entity);

}