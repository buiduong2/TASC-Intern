package com.backend.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.order.model.OrderAddress;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {

}
