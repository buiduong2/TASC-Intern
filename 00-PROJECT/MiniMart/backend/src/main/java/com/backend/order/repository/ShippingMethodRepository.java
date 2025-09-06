package com.backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.order.model.ShippingMethod;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {

}
