package com.employee_manager.services.impl;

import com.employee_manager.dto.EmployeeDTO;
import com.employee_manager.entities.Employee;
import com.employee_manager.mapper.EmployeeMapper;
import com.employee_manager.repositories.EmployeeRepository;
import com.employee_manager.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    @Override
    public EmployeeDTO save(EmployeeDTO dto) {
        Employee entity = mapper.toEntity(dto);
        Employee saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public EmployeeDTO update(Long id, EmployeeDTO dto) {
        Employee existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        mapper.updateEntityFromDTO(dto, existing);

        Employee updated = repository.save(existing);
        return mapper.toDTO(updated);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> findById(Long id) {
        return repository.findById(id).map(mapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDTO);
    }
}