package com.backend.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.common.model.BaseAddress;

public interface AddressRepository extends JpaRepository<BaseAddress, Long> {
    
}
