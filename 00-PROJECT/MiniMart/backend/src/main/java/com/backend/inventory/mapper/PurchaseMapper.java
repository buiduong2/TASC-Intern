package com.backend.inventory.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.backend.inventory.dto.req.CreatePurchaseReq;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.model.PurchaseItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = {
        PurchaseItemMapper.class }, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface PurchaseMapper {

    @Mapping(target = "audit", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "purchaseItems", source = "items")
    Purchase toEntity(CreatePurchaseReq req);

    @Mapping(target = "createdAt", source = "audit.createdAt")
    @Mapping(target = "totalQuantity", expression = "java( getTotalQuantity(purchase) )")
    @Mapping(target = "totalCostPrice", expression = "java( getTotalCostPrice(purchase) )")
    PurchaseDTO toDTO(Purchase purchase);

    default int getTotalQuantity(Purchase purchase) {
        List<PurchaseItem> items = purchase.getPurchaseItems();
        int sum = 0;
        for (PurchaseItem item : items) {
            sum += item.getQuantity();
        }
        return sum;
    }

    default BigDecimal getTotalCostPrice(Purchase purchase) {
        List<PurchaseItem> items = purchase.getPurchaseItems();
        BigDecimal sum = BigDecimal.ZERO;
        for (PurchaseItem item : items) {
            sum = sum.add(item.getCostPrice());
        }
        return sum;
    }

}
