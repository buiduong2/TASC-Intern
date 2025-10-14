package com.order_service.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ShippingMethod {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;

    @Column(precision = 19, scale = 2)
    private BigDecimal cost;
}
