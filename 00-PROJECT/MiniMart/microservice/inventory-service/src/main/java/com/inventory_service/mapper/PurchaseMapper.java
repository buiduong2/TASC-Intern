package com.inventory_service.mapper;

import java.math.BigDecimal;
import java.util.List;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.common.mapper.ToEntity;
import com.inventory_service.dto.req.CreatePurchaseReq;
import com.inventory_service.dto.res.PurchaseSummaryDTO;
import com.inventory_service.model.Purchase;
import com.inventory_service.model.PurchaseItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED, uses = PurchaseItemMapper.class)
public interface PurchaseMapper {

    @ToEntity
    @Mapping(target = "status", ignore = true)
    Purchase toEntity(CreatePurchaseReq req);

    @Mapping(target = "totalQuantity", expression = "java( getTotalQuantity(purchase) )")
    @Mapping(target = "totalCostPrice", expression = "java( getTotalCostPrice(purchase) )")
    PurchaseSummaryDTO toSummaryDTO(Purchase purchase);

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
