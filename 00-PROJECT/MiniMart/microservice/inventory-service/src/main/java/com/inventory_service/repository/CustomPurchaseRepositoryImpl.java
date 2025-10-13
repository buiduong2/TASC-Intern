package com.inventory_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

import com.inventory_service.dto.res.PurchaseDetailDTO;
import com.inventory_service.dto.res.PurchaseSummaryDTO;
import com.inventory_service.model.Purchase;
import com.inventory_service.model.PurchaseItem;
import com.inventory_service.model.PurchaseItem_;
import com.inventory_service.model.Purchase_;
import com.inventory_service.utils.PurchaseOrderConverter;

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
    public Page<PurchaseSummaryDTO> findAdminAll(Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<PurchaseSummaryDTO> query = builder.createQuery(PurchaseSummaryDTO.class);
        Root<Purchase> purchase = query.from(Purchase.class);
        Join<Purchase, PurchaseItem> purchaseItem = purchase.join(Purchase_.purchaseItems, JoinType.LEFT);

        query.groupBy(purchase.get(Purchase_.id));

        query.select(builder.construct(PurchaseSummaryDTO.class,
                purchase.get(Purchase_.id),
                purchase.get(Purchase_.supplier),
                builder.coalesce(builder.sum(purchaseItem.get(PurchaseItem_.quantity)), 0),
                builder.coalesce(builder.sum(purchaseItem.get(PurchaseItem_.costPrice)), 0),
                purchase.get(Purchase_.status),
                purchase.get(Purchase_.createdAt))

        );

        if (pageable.getSort() != null) {
            List<Order> orders = purchaseOrderConverter.convert(pageable.getSort().toList(), purchase, query, builder);
            query.orderBy(orders);
        }

        List<PurchaseSummaryDTO> list = em.createQuery(query)
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

    @Override
    public Optional<PurchaseDetailDTO> findAdminById(long id) {
        System.out.println(id);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<PurchaseDetailDTO> query = builder.createQuery(PurchaseDetailDTO.class);
        Root<Purchase> purchase = query.from(Purchase.class);
        Join<Purchase, PurchaseItem> purchaseItem = purchase.join(Purchase_.purchaseItems, JoinType.LEFT);

        query.groupBy(purchase.get(Purchase_.id));
        query.select(builder.construct(PurchaseDetailDTO.class,
                purchase.get(Purchase_.id),
                purchase.get(Purchase_.supplier),
                builder.coalesce(builder.sum(purchaseItem.get(PurchaseItem_.quantity)), 0),
                builder.coalesce(builder.sum(purchaseItem.get(PurchaseItem_.costPrice)), 0),
                purchase.get(Purchase_.status),
                purchase.get(Purchase_.createdAt),
                purchase.get(Purchase_.updatedAt),
                purchase.get(Purchase_.createdById),
                purchase.get(Purchase_.updatedById))

        );

        query.where(builder.equal(purchase.get(Purchase_.id), id));

        return em.createQuery(query).getResultList().stream().findFirst();
    }

}
