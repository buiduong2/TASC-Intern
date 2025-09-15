package com.backend.inventory.utils;

import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.backend.common.utils.AbstractOrderConverter;
import com.backend.common.utils.CriteriaApiUtils;
import com.backend.common.utils.OrderSupplier;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.model.PurchaseItem_;
import com.backend.inventory.model.Purchase_;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

@Component
public class PurchaseOrderConverter extends AbstractOrderConverter<Purchase> {

    @Override
    public Map<String, OrderSupplier<Purchase>> getSuppliers() {
        return Map.of(
                "totalQuantity", this::sortByTotalQuantity,
                "totalCostPrice", this::sortByTotalCostPrice);
    }

    private Order sortByTotalQuantity(Sort.Order order, Root<Purchase> root, CriteriaQuery<?> query,
            CriteriaBuilder builder) {

        Join<Purchase, PurchaseItem> purchaseItems = CriteriaApiUtils
                .getOrCreateJoin(root, Purchase_.purchaseItems, JoinType.LEFT);

        return CriteriaApiUtils.buildOrder(order, builder.sum(purchaseItems.get(PurchaseItem_.quantity)), builder);
    }

    private Order sortByTotalCostPrice(Sort.Order order, Root<Purchase> root, CriteriaQuery<?> query,
            CriteriaBuilder builder) {

        Join<Purchase, PurchaseItem> purchaseItems = CriteriaApiUtils
                .getOrCreateJoin(root, Purchase_.purchaseItems, JoinType.LEFT);

        return CriteriaApiUtils.buildOrder(order, builder.sum(purchaseItems.get(PurchaseItem_.costPrice)), builder);
    }
}
