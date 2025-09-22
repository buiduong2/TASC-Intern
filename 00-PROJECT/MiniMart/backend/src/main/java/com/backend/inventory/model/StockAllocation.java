package com.backend.inventory.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.order.model.OrderItem;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import lombok.Getter;
import lombok.Setter;

@NamedEntityGraph(name = "StockAllocation.withRelations", attributeNodes = {
        @NamedAttributeNode(value = "orderItem", subgraph = "orderItem-subgraph"),
        @NamedAttributeNode("purchaseItem")
}, subgraphs = {
        @NamedSubgraph(name = "orderItem-subgraph", attributeNodes = {
                @NamedAttributeNode("product")
        })
})
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class StockAllocation {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    private PurchaseItem purchaseItem;

    private int allocatedQuantity;

    @Enumerated(EnumType.STRING)
    private StockAllocationStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
}
