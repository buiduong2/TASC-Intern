package com.backend.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.common.model.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    
}
