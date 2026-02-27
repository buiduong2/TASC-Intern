package com.employee_manager.services;

import com.employee_manager.dto.EmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface EmployeeService {

    EmployeeDTO save(EmployeeDTO dto);

    EmployeeDTO update(Long id, EmployeeDTO dto);

    void delete(Long id);

    Optional<EmployeeDTO> findById(Long id);

    Page<EmployeeDTO> findAll(Pageable pageable);
}