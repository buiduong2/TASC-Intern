package com.order_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue
    private Long id;

    private String details;

    private String city;

    private String area;

    private String firstName;

    private String lastName;

    private String phone;

    @OneToOne(mappedBy = "address")
    private Order order;
}
