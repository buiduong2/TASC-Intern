package com.backend.order.model;

import java.util.List;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.backend.common.model.Audit;
import com.backend.user.model.Customer;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
public class Order {

    @Id
    @GeneratedValue
    private Long id;

    // Snapshot
    private double total;

    private String message;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToOne(cascade = CascadeType.ALL)
    private ShippingMethod shippingMethod;

    @OneToOne(cascade = CascadeType.ALL)
    private OrderAddress address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @OneToOne(cascade = CascadeType.ALL)
    private Payment payment;

    @Embedded
    private Audit audit = new Audit();

    @ManyToOne
    private Customer customer;

    @Version
    private long version;

}
