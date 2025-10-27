package com.order_service.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.order_service.enums.OrderStatus;
import com.order_service.enums.PaymentMethod;
import com.order_service.enums.PaymentStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@NamedEntityGraph(name = Order.namedGraph_WithItem, attributeNodes = {
        @NamedAttributeNode(value = "orderItems")
})

@NamedEntityGraph(name = Order.namedGraph_WithItemAndAddress, attributeNodes = {
        @NamedAttributeNode(value = "orderItems"),
        @NamedAttributeNode(value = "address"),
})

@NamedEntityGraph(name = Order.namedGraph_CientDetail, attributeNodes = {
        @NamedAttributeNode(value = "address"),
        @NamedAttributeNode(value = "shippingMethod"),
})
@NamedEntityGraph(name = Order.namedGraphClientSummary, attributeNodes = {

        @NamedAttributeNode(value = "shippingMethod"),
})

@Entity
@Getter
@Setter
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {

    public static final String namedGraph_WithItem = "Order.WithOrderItems";
    public static final String namedGraph_WithItemAndAddress = "Order.WithItems-Address";
    public static final String namedGraph_CientDetail = "Order.client-detail";
    public static final String namedGraphClientSummary = "Order.client-summary";

    @Id
    @GeneratedValue
    private Long id;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    @Column(precision = 19, scale = 2)
    private BigDecimal totalCost = BigDecimal.ZERO;

    private String message;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    private ShippingMethod shippingMethod;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Address address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @Column(unique = true)
    private Long paymentId;

    private PaymentStatus paymentStatus;

    private PaymentMethod paymentMethod;

    private Long userId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private Long createdById;

    @LastModifiedBy
    private Long updatedById;

    @Version
    private long version;

    public void addOrderItem(OrderItem orderItem) {
        if (this.orderItems == null) {
            this.orderItems = new ArrayList<>();
        }
        this.orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

}
