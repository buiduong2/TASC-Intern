package com.backend.inventory.utils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import com.backend.common.utils.CriteriaApiUtils;
import com.backend.inventory.dto.req.StockAllocationFilter;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.model.PurchaseItem_;
import com.backend.inventory.model.Purchase_;
import com.backend.inventory.model.StockAllocation;
import com.backend.inventory.model.StockAllocation_;
import com.backend.order.model.OrderItem;
import com.backend.order.model.OrderItem_;
import com.backend.order.model.Order_;
import com.backend.product.model.Product_;

import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockAllocationSpecs {

    public Specification<StockAllocation> afterCreatedAt(LocalDateTime startDate) {
        return (root, query, builder) -> {
            return builder.greaterThanOrEqualTo(root.get(StockAllocation_.createdAt), startDate);
        };
    }

    public Specification<StockAllocation> byOrderId(long orderId) {
        return (root, query, builder) -> {

            Join<StockAllocation, OrderItem> orderItem = CriteriaApiUtils.getOrCreateJoin(root,
                    StockAllocation_.orderItem);
            return builder.equal(orderItem.get(OrderItem_.order).get(Order_.id), orderId);
        };
    }

    public Specification<StockAllocation> byPurchaseId(long purchaseId) {
        return (root, query, builder) -> {
            Join<StockAllocation, PurchaseItem> purchaseItem = CriteriaApiUtils.getOrCreateJoin(root,
                    StockAllocation_.purchaseItem);
            return builder.equal(purchaseItem.get(PurchaseItem_.purchase).get(Purchase_.id), purchaseId);
        };
    }

    public Specification<StockAllocation> byProductId(long productId) {
        return (root, query, builder) -> {

            Join<StockAllocation, OrderItem> orderItem = CriteriaApiUtils.getOrCreateJoin(root,
                    StockAllocation_.orderItem);
            return builder.equal(orderItem.get(OrderItem_.product).get(Product_.id), productId);
        };
    }

    public Specification<StockAllocation> beforeCreatedAt(LocalDateTime endDate) {
        return (root, query, builder) -> {
            return builder.lessThanOrEqualTo(root.get(StockAllocation_.createdAt), endDate);
        };
    }

    public Specification<StockAllocation> byFilter(StockAllocationFilter filter) {
        if (filter == null) {
            return (root, query, builder) -> builder.conjunction();
        }
        List<Specification<StockAllocation>> specs = new ArrayList<>();

        if (filter.getStartDate() != null) {
            specs.add(afterCreatedAt(filter.getStartDate()));
        }

        if (filter.getEndDate() != null) {
            specs.add(beforeCreatedAt(filter.getEndDate()));
        }

        if (filter.getOrderId() != null) {
            specs.add(byOrderId(filter.getOrderId()));
        }

        if (filter.getProductId() != null) {
            specs.add(byProductId(filter.getProductId()));
        }

        if (filter.getPurchaseId() != null) {
            specs.add(byPurchaseId(filter.getPurchaseId()));
        }

        if (specs.isEmpty()) {
            return (root, query, builder) -> builder.conjunction();
        }

        return Specification.allOf(specs);
    }

}
