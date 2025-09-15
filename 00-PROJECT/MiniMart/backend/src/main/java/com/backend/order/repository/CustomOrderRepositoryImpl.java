package com.backend.order.repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.backend.order.dto.res.OrderAdminDTO;
import com.backend.order.dto.res.OrderFilter;
import com.backend.order.model.Order;

import jakarta.persistence.EntityManager;
import jakarta.persistence.StoredProcedureQuery;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomOrderRepositoryImpl implements CustomOrderRepository {

    private final EntityManager em;

    private final Map<String, String> sortCol = Map.of(
            "status", "o.status",
            "paymentMethod", "payment_method",
            "shippingMethod", "shipping_method",
            "totalPrice", "total_price",
            "totalCost", "total_cost",
            "revenue", "revenue",
            "customerId", "o.customer_id",
            "createdAt", "o.created_at",
            "updatedAt", "o.updated_at");

    @Override
    public Page<OrderAdminDTO> findAdminAll(OrderFilter filter, Pageable pageable) {
        StoredProcedureQuery query = em.createNamedStoredProcedureQuery(Order.NamedProcedure_PAGE_ADMIN);
        // set IN params
        query.setParameter("p_order_id", filter.getId());
        query.setParameter("p_status", filter.getStatus());
        query.setParameter("p_payment_method_name", filter.getPaymentMethod());
        query.setParameter("p_shipping_method_id", filter.getShippingMethodId());
        query.setParameter("p_customer_id", filter.getCustomerId());
        query.setParameter("p_created_from", filter.getCreatedFrom());
        query.setParameter("p_created_to", filter.getCreatedTo());
        query.setParameter("p_min_total_price", filter.getMinTotalPrice());
        query.setParameter("p_max_total_price", filter.getMaxTotalPrice());
        query.setParameter("p_min_total_cost", filter.getMinTotalCost());
        query.setParameter("p_max_total_cost", filter.getMaxTotalCost());

        // pageable
        query.setParameter("p_page_size", pageable.getPageSize());
        query.setParameter("p_page_offset", (int) pageable.getOffset());

        // SOrt
        String sortClause = pageable.getSort().stream()
                .map(order -> {
                    String col = sortCol.get(order.getProperty());
                    if (col == null) {
                        return null;
                    }
                    return col + " " + order.getDirection().name();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
        query.setParameter("p_sorts", sortClause);
        @SuppressWarnings("unchecked")
        List<OrderAdminDTO> resultList = query.getResultList();
        Long total = (Long) query.getOutputParameterValue("page_total");

        return new PageImpl<>(resultList, pageable, total == null ? 0 : total);

    }

}
