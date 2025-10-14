package com.order_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.order_service.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {

}
