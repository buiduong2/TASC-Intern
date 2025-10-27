package com.order_service.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.order_service.enums.OrderStatus;
import com.order_service.model.Order;

import jakarta.persistence.LockModeType;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(value = Order.namedGraph_WithItem, type = EntityGraphType.FETCH)
    @Query("""
            FROM Order AS o
            WHERE o.id = ?1 AND o.userId = ?2
            """)
    Optional<Order> findWithItemsByIdAndUserIdForUpdate(long id, long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(value = Order.namedGraph_WithItemAndAddress, type = EntityGraphType.FETCH)
    @Query("""
            FROM Order AS o
            WHERE o.id = ?1 AND o.userId = ?2
            """)
    Optional<Order> findByIdAndUserIdForDelete(long id, long userId);

    @EntityGraph(value = Order.namedGraph_WithItem, type = EntityGraphType.FETCH)
    @Query("""
            FROM Order AS o
            WHERE o.id = ?1 AND o.userId = ?2
            """)
    Optional<Order> findByIdAndUserIdWithItem(long id, long userId);

    @EntityGraph(value = Order.namedGraph_CientDetail, type = EntityGraphType.FETCH)
    @Query("""
            FROM Order AS o
            WHERE o.id = ?1 AND o.userId = ?2
            """)
    Optional<Order> findByIdAndUserIdForClientDetail(long orderId, long userId);

    // GET

    @EntityGraph(value = Order.namedGraphClientSummary, type = EntityGraphType.FETCH)
    @Query("""
            FROM Order AS o
            WHERE o.id = ?1 AND o.userId = ?2
            """)
    Page<Order> findByUserIdForCLientSummary(long userId, Pageable pageable);

    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(value = Order.namedGraph_WithItem, type = EntityGraphType.FETCH)
    @Query("""
            FROM Order AS o
                  WHERE o.updatedAt > ?2 AND o.status = ?1
            """)
    List<Order> findByStatusAndUpdatedAtBefore(OrderStatus status, LocalDateTime updatedAt);

}
