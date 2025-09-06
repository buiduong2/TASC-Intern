package com.backend.order.model;

import com.backend.common.model.BaseAddress;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class OrderAddress extends BaseAddress {

    @OneToOne(mappedBy = "address")
    private Order order;
}