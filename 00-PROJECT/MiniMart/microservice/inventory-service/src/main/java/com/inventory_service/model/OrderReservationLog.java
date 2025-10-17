package com.inventory_service.model;

import com.inventory_service.enums.OrderReservationLogStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "product_id", "orer_id" }) })
@Entity
public class OrderReservationLog {

    @Id
    @GeneratedValue
    private Long id;

    private Long productId;

    private Long orderId;

    private Long orderItemId;

    private int quantityReserved;

    private OrderReservationLogStatus status;
}
