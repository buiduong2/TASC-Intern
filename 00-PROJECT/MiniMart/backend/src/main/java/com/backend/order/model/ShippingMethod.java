package com.backend.order.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ShippingMethod {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String description;
    
    private double cost;
}
