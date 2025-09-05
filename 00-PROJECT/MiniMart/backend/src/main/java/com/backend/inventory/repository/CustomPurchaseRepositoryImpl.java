package com.backend.inventory.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

import com.backend.common.model.Audit_;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.model.PurchaseItem_;
import com.backend.inventory.model.Purchase_;
import com.backend.inventory.utils.PurchaseOrderConverter;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Root;

@Repository
public class CustomPurchaseRepositoryImpl implements CustomPurchaseRepository {

    private final EntityManager em;

    private final PurchaseOrderConverter purchaseOrderConverter;

    public CustomPurchaseRepositoryImpl(JpaContext context, PurchaseOrderConverter purchaseOrderConverter) {
        this.em = context.getEntityManagerByManagedType(Purchase.class);
        this.purchaseOrderConverter = purchaseOrderConverter;
    }

    @Override
    public Page<PurchaseDTO> findAdminAll(Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<PurchaseDTO> query = builder.createQuery(PurchaseDTO.class);
        Root<Purchase> purchase = query.from(Purchase.class);
        Join<Purchase, PurchaseItem> purchaseItem = purchase.join(Purchase_.purchaseItems, JoinType.LEFT);

        query.groupBy(purchase.get(Purchase_.id));
        query.select(builder.construct(PurchaseDTO.class,
                purchase.get(Purchase_.id),
                purchase.get(Purchase_.audit).get(Audit_.createdAt),
                purchase.get(Purchase_.supplier),
                builder.sum(purchaseItem.get(PurchaseItem_.quantity)),

                builder.sum(purchaseItem.get(PurchaseItem_.costPrice)))

        );

        if (pageable.getSort() != null) {
            List<Order> orders = purchaseOrderConverter.convert(pageable.getSort().toList(), purchase, query, builder);
            query.orderBy(orders);
        }

        List<PurchaseDTO> list = em.createQuery(query)
                .setMaxResults(pageable.getPageSize())
                .setFirstResult((int) pageable.getOffset())
                .getResultList();

        return new PageImpl<>(list, pageable, getTotalItem());
    }

    private long getTotalItem() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<Purchase> purchase = query.from(Purchase.class);
        query.select(builder.count(purchase));

        return em.createQuery(query).getSingleResult();
    }

}
