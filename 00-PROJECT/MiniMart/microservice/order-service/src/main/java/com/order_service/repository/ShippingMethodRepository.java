package com.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order_service.model.ShippingMethod;

public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {

}
