package com.backend.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaContext;
import org.springframework.stereotype.Repository;

import com.backend.common.model.Audit;
import com.backend.common.model.Audit_;
import com.backend.inventory.dto.res.PurchaseDTO;
import com.backend.inventory.dto.res.PurchaseDetailDTO;
import com.backend.inventory.model.Purchase;
import com.backend.inventory.model.PurchaseItem;
import com.backend.inventory.model.PurchaseItem_;
import com.backend.inventory.model.Purchase_;
import com.backend.inventory.utils.PurchaseOrderConverter;
import com.backend.user.model.User;
import com.backend.user.model.User_;

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

    @Override
    public Optional<PurchaseDetailDTO> findAdminById(long id) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<PurchaseDetailDTO> query = builder.createQuery(PurchaseDetailDTO.class);
        Root<Purchase> purchase = query.from(Purchase.class);
        Join<Purchase, PurchaseItem> purchaseItem = purchase.join(Purchase_.purchaseItems, JoinType.LEFT);
        Join<Purchase, Audit> audit = purchase.join(Purchase_.audit);
        Join<Audit, User> createdBy = audit.join(Audit_.createdBy);
        Join<Audit, User> updatedBy = audit.join(Audit_.updatedBy);

        query.groupBy(purchase.get(Purchase_.id));
        query.select(builder.construct(PurchaseDetailDTO.class,
                purchase.get(Purchase_.id),
                purchase.get(Purchase_.supplier),
                builder.sum(purchaseItem.get(PurchaseItem_.quantity)),
                builder.sum(purchaseItem.get(PurchaseItem_.costPrice)),
                audit.get(Audit_.createdAt),
                audit.get(Audit_.updatedAt),
                builder.construct(PurchaseDetailDTO.UserDTO.class,
                        createdBy.get(User_.id),
                        createdBy.get(User_.fullName)),
                builder.construct(PurchaseDetailDTO.UserDTO.class,
                        updatedBy.get(User_.id),
                        updatedBy.get(User_.fullName)))

        );

        query.where(builder.equal(purchase.get(Purchase_.id), id));

        return em.createQuery(query).getResultList().stream().findFirst();
    }

}
