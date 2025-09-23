package com.backend.common.repository.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.backend.common.dto.LowStockProductFilter;
import com.backend.common.dto.ProductLowStockDTO;
import com.backend.common.dto.ProfitReportDTO.ProfitDataDTO;
import com.backend.common.dto.RevenueFilter;
import com.backend.common.dto.RevenueReportDTO.RevenueDataDTO;
import com.backend.common.dto.TopProductDTO;
import com.backend.common.dto.TopProductFilter;
import com.backend.common.dto.ProductLowStockDTO.CategoryDTO;
import com.backend.common.model.Audit_;
import com.backend.common.repository.ReportRepository;
import com.backend.common.utils.CriteriaApiUtils;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.model.PurchaseItem_;
import com.backend.inventory.model.Stock;
import com.backend.inventory.model.StockAllocation;
import com.backend.inventory.model.StockAllocation_;
import com.backend.inventory.model.Stock_;
import com.backend.order.model.Order;
import com.backend.order.model.OrderItem;
import com.backend.order.model.OrderItem_;
import com.backend.order.model.OrderStatus;
import com.backend.order.model.Order_;
import com.backend.order.model.PaymentStatus;
import com.backend.order.model.Payment_;
import com.backend.product.model.Category;
import com.backend.product.model.Category_;
import com.backend.product.model.Product;
import com.backend.product.model.ProductImage;
import com.backend.product.model.ProductImage_;
import com.backend.product.model.Product_;
import com.backend.user.model.Customer_;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryImpl implements ReportRepository {

    private final EntityManager em;

    @Override
    public BigDecimal getTotalRevenue(RevenueFilter filter) {

        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BigDecimal> query = builder.createQuery(BigDecimal.class);
        Root<Order> order = query.from(Order.class);

        query.select(builder.coalesce(builder.sum(order.get(Order_.total).as(BigDecimal.class)), new BigDecimal(0d)));
        query.where(baseFilter(filter).and(byRelationId(filter)).toPredicate(order, query, builder));

        return em.createQuery(query).getSingleResult();
    }

    @Override
    public List<RevenueDataDTO> getRevenueGrouping(RevenueFilter filter) {
        String groupBy = filter.getGroupBy();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<RevenueDataDTO> query = builder.createQuery(RevenueDataDTO.class);
        Root<Order> order = query.from(Order.class);

        Predicate byFilter = byRelationId(filter).toPredicate(order, query, builder);
        Predicate baseFilter = baseFilter(filter).toPredicate(order, query, builder);
        System.out.println("by FIlter" + byFilter);
        System.out.println("baseFilter" + baseFilter);
        query.where(builder.and(byFilter, baseFilter));

        Path<LocalDateTime> createdAtPath = order.get(Order_.audit).get(Audit_.createdAt);
        Expression<BigDecimal> totalRevenueExpr = builder.sum(order.get(Order_.total));

        if (groupBy.equals("DAY")) {
            Expression<String> labelExpr = builder.function("DATE", LocalDate.class, createdAtPath)
                    .as(String.class);
            query.groupBy(labelExpr);
            query.select(builder.construct(RevenueDataDTO.class, labelExpr, totalRevenueExpr));
            query.orderBy(builder.asc(labelExpr));
        } else {
            Expression<String> yearExpr = builder.function("DATE_PART", Integer.class,
                    builder.literal("year"), createdAtPath)
                    .as(String.class);

            Expression<String> monthExpr = builder.function("DATE_PART", Integer.class,
                    builder.literal("month"), createdAtPath).as(String.class);

            if (groupBy.equals("MONTH")) {
                Expression<String> labelExpr = builder.concat(builder.concat(yearExpr, "-"), monthExpr);
                query.groupBy(yearExpr, monthExpr);

                query.select(builder.construct(RevenueDataDTO.class, labelExpr, totalRevenueExpr));
                query.orderBy(builder.asc(yearExpr), builder.asc(monthExpr));
            } else if (groupBy.equals("YEAR")) {
                query.groupBy(yearExpr);
                query.select(builder.construct(RevenueDataDTO.class, yearExpr, totalRevenueExpr));
                query.orderBy(builder.asc(yearExpr));
            } else {
                throw new IllegalArgumentException("Filter['groupBy']  value not valid");
            }
        }

        return em.createQuery(query).getResultList();
    }

    private Specification<Order> baseFilter(RevenueFilter filter) {
        Specification<Order> createdAtBetween = isOrderCreatedAtBetween(filter.getStartDate(), filter.getEndDate());
        Specification<Order> orderIsPaid = orderIsPaid();
        Specification<Order> orderIsCompleted = orderIsCompleted();
        return Specification.allOf(createdAtBetween, orderIsCompleted, orderIsPaid);

    }

    private Specification<Order> isOrderCreatedAtBetween(LocalDateTime startdate, LocalDateTime endDate) {
        return (root, query, builder) -> {
            return builder.between(root.get(Order_.audit).get(Audit_.createdAt), startdate, endDate);
        };
    }

    private Specification<Order> isOrderInPeriod(TopProductFilter filter) {
        final LocalDateTime start;
        final LocalDateTime end;

        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            start = filter.getStartDate();
            end = filter.getEndDate();
        } else {
            end = LocalDateTime.now();
            if (filter.getPeriod().equals("DAY")) {
                start = end.minusDays(1);
            } else if (filter.getPeriod().equals("WEEK")) {
                start = end.minusMonths(1);
            } else if (filter.getPeriod().equals("MONTH")) {
                start = end.minusWeeks(1);
            } else { // YEAR
                start = end.minusYears(1);
            }
        }

        return (root, query, builder) -> {
            return builder.between(root.get(Order_.audit).get(Audit_.createdAt), start, end);
        };
    }

    private Specification<Order> orderIsCompleted() {
        return (root, query, builder) -> {
            return builder.equal(root.get(Order_.status), OrderStatus.COMPLETED);
        };
    }

    private Specification<Order> orderIsPaid() {
        return (root, query, builder) -> {
            return builder.equal(root.get(Order_.payment).get(Payment_.status), PaymentStatus.PAID);
        };
    }

    private Specification<Order> byRelationId(RevenueFilter filter) {
        List<Specification<Order>> specs = new ArrayList<>();
        if (filter.getCategoryId() != null) {
            specs.add(isCategoryId(filter.getCategoryId()));
        }

        if (filter.getProductId() != null) {
            specs.add(isProductId(filter.getProductId()));
        }

        if (filter.getCustomerId() != null) {
            specs.add(isCustomerId(filter.getCustomerId()));
        }

        if (specs.isEmpty()) {
            return (root, query, cb) -> cb.conjunction();
        }

        return Specification.allOf(specs);
    }

    private Specification<Order> isCustomerId(long customerId) {
        return (root, query, builder) -> {
            return builder.equal(root.get(Order_.customer).get(Customer_.id), customerId);
        };
    }

    private Specification<Order> isCategoryId(long categoryId) {
        return (root, query, builder) -> {
            Join<Order, OrderItem> orderItem = CriteriaApiUtils.getOrCreateJoin(root,
                    Order_.orderItems);

            Join<OrderItem, Product> product = CriteriaApiUtils.getOrCreateJoin(orderItem,
                    OrderItem_.product);

            return builder.equal(product.get(Product_.category).get(Category_.id), categoryId);
        };
    }

    private Specification<Order> isProductId(long productId) {
        return (root, query, builder) -> {
            Join<Order, OrderItem> orderItem = CriteriaApiUtils.getOrCreateJoin(root,
                    Order_.orderItems);

            return builder.equal(orderItem.get(OrderItem_.product).get(Product_.id), productId);
        };
    }

    @Override
    public List<ProfitDataDTO> getTotalProfit(RevenueFilter filter) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ProfitDataDTO> query = builder.createQuery(ProfitDataDTO.class);

        // FROM
        Root<Order> root = query.from(Order.class);
        Join<Order, OrderItem> oi = root.join(Order_.orderItems);
        Join<OrderItem, StockAllocation> sa = oi.join(OrderItem_.allocations);
        Join<StockAllocation, PurchaseItem> pi = sa.join(StockAllocation_.purchaseItem);

        // SELECT
        Expression<BigDecimal> totalRevenueExpr = builder.coalesce(builder
                .sum(builder.prod(sa.get(StockAllocation_.allocatedQuantity), oi.get(OrderItem_.unitPrice))
                        .as(BigDecimal.class)),
                builder.literal(BigDecimal.ZERO));
        Expression<BigDecimal> totalCostExpr = builder.coalesce(builder
                .sum(builder.prod(sa.get(StockAllocation_.allocatedQuantity), pi.get(PurchaseItem_.costPrice))
                        .as(BigDecimal.class)),
                builder.literal(BigDecimal.ZERO));
        Expression<BigDecimal> totalProfitExpr = builder.diff(totalRevenueExpr, totalCostExpr);

        // WHERE
        query.where(baseFilter(filter).and(byRelationId(filter)).toPredicate(root, query, builder));

        // GROUP BY
        String groupBy = filter.getGroupBy();
        Path<LocalDateTime> createdAtPath = sa.get(StockAllocation_.createdAt);

        if (groupBy.equals("NONE")) {
            query.select(builder.construct(
                    ProfitDataDTO.class,
                    builder.literal("Total"), totalRevenueExpr, totalCostExpr, totalProfitExpr));

        } else if (groupBy.equals("DAY")) {
            Expression<String> labelExpr = builder
                    .function("DATE", LocalDate.class, createdAtPath)
                    .as(String.class);
            query.groupBy(labelExpr);
            query.select(builder.construct(ProfitDataDTO.class, labelExpr, totalRevenueExpr, totalCostExpr,
                    totalProfitExpr));
            query.orderBy(builder.asc(labelExpr));
        } else {
            Expression<String> yearExpr = builder.function("DATE_PART", Integer.class,
                    builder.literal("year"), createdAtPath)
                    .as(String.class);

            Expression<String> monthExpr = builder.function("DATE_PART", Integer.class,
                    builder.literal("month"), createdAtPath).as(String.class);

            if (groupBy.equals("MONTH")) {
                Expression<String> labelExpr = builder.concat(builder.concat(yearExpr, "-"), monthExpr);
                query.groupBy(yearExpr, monthExpr);

                query.select(builder.construct(ProfitDataDTO.class, labelExpr,
                        totalRevenueExpr, totalCostExpr, totalProfitExpr));
                query.orderBy(builder.asc(yearExpr), builder.asc(monthExpr));
            } else if (groupBy.equals("YEAR")) {
                query.groupBy(yearExpr);
                query.select(builder.construct(ProfitDataDTO.class, yearExpr,
                        totalRevenueExpr, totalCostExpr, totalProfitExpr));
                query.orderBy(builder.asc(yearExpr));
            } else {
                throw new IllegalArgumentException("Filter['groupBy']  value not valid");
            }
        }

        return em.createQuery(query).getResultList();
    }

    @Override
    public List<TopProductDTO> getTopProduct(TopProductFilter filter) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<TopProductDTO> query = builder.createQuery(TopProductDTO.class);

        // from
        Root<Order> root = query.from(Order.class);
        Join<Order, OrderItem> oi = root.join(Order_.orderItems);
        Join<OrderItem, StockAllocation> sa = oi.join(OrderItem_.allocations);
        Join<StockAllocation, PurchaseItem> pi = sa.join(StockAllocation_.purchaseItem);
        Join<PurchaseItem, Product> p = pi.join(PurchaseItem_.product);
        Join<Product, ProductImage> i = p.join(Product_.image);

        // where
        Specification<Order> spec = isOrderInPeriod(filter)
                .and(orderIsPaid());
        if (filter.getStartDate() != null && filter.getEndDate() != null) {
            spec = spec.and(isOrderCreatedAtBetween(filter.getStartDate(), filter.getEndDate()));
        }
        if (filter.getCategoryId() != null) {
            spec = spec.and(isCategoryId(filter.getCategoryId()));
        }
        // SELECT

        Expression<Long> totalSoldExpr = builder
                .coalesce(builder.sumAsLong(sa.get(StockAllocation_.allocatedQuantity)), 0)
                .as(Long.class);
        Expression<BigDecimal> totalRevenueExpr = builder.coalesce(builder
                .sum(builder.prod(sa.get(StockAllocation_.allocatedQuantity), oi.get(OrderItem_.unitPrice))
                        .as(BigDecimal.class)),
                builder.literal(BigDecimal.ZERO));
        Expression<BigDecimal> totalCostExpr = builder.coalesce(builder
                .sum(builder.prod(sa.get(StockAllocation_.allocatedQuantity), pi.get(PurchaseItem_.costPrice))
                        .as(BigDecimal.class)),
                builder.literal(BigDecimal.ZERO));
        Expression<BigDecimal> totalProfitExpr = builder.diff(totalRevenueExpr, totalCostExpr);

        query.select(builder.construct(TopProductDTO.class,
                p.get(Product_.id),
                p.get(Product_.name),
                i.get(ProductImage_.url),
                totalSoldExpr,
                totalRevenueExpr,
                totalProfitExpr

        ));

        // GROUP BY

        query.groupBy(p.get(Product_.id),
                p.get(Product_.name),
                i.get(ProductImage_.url));

        // ORder BY
        switch (filter.getMetric()) {
            case "quantity":
                query.orderBy(builder.desc(totalSoldExpr));
                break;
            case "revenue":
                query.orderBy(builder.desc(totalRevenueExpr));
                break;
            case "profit":
                query.orderBy(builder.desc(totalProfitExpr));
                break;
            default:
                throw new IllegalArgumentException("TopProductFilter.metric does not expected ");
        }

        return em.createQuery(query)
                .setMaxResults(filter.getLimit())
                .getResultList();
    }

    @Override
    public List<ProductLowStockDTO> getLowStockProduct(LowStockProductFilter filter, PageRequest pageRequest) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<ProductLowStockDTO> query = builder.createQuery(ProductLowStockDTO.class);

        // FROM
        Root<Product> root = query.from(Product.class);
        Join<Product, Category> c = root.join(Product_.category, JoinType.LEFT);
        Join<Product, Stock> s = root.join(Product_.stock, JoinType.LEFT);

        Specification<Product> spec = (r, q, b) -> b.conjunction();
        // WHERE
        if (filter.getCategoryId() != null) {
            spec.and((r, q, b) -> b.equal(r.get(Product_.category).get(Category_.id), filter.getCategoryId()));
        }

        if (filter.getStatus() != null) {
            spec.and((r, q, b) -> b.equal(r.get(Product_.status), filter.getStatus()));
        }
        spec.and((r, q, b) -> b.lessThanOrEqualTo(s.get(Stock_.quantity), filter.getThreshold()));

        // SELECT
        query.select(builder.construct(ProductLowStockDTO.class,
                root.get(Product_.id),
                root.get(Product_.name),
                builder.construct(CategoryDTO.class, c.get(Category_.id), c.get(Category_.name)),
                builder.coalesce(s.get(Stock_.quantity), 0),
                root.get(Product_.status),
                root.get(Product_.salePrice),
                root.get(Product_.compareAtPrice)));

        // ORDER BY

        query.orderBy(builder.desc(s.get(Stock_.quantity)));

        return em.createQuery(query)
                .setMaxResults(pageRequest.getPageSize())
                .setFirstResult((int) pageRequest.getOffset())
                .getResultList();
    }

}
