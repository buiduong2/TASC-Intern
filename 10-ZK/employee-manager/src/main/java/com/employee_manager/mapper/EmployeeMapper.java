package com.employee_manager.mapper;

import com.employee_manager.dto.EmployeeDTO;
import com.employee_manager.entities.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    public EmployeeDTO toDTO(Employee entity) {
        if (entity == null) return null;

        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(entity.getId());
        dto.setFullName(entity.getFullName());
        dto.setJob(entity.getJob());
        dto.setGender(entity.isGender());
        dto.setBirthDate(entity.getBirthDate());
        dto.setAvatarSrc(entity.getAvatarSrc());

        return dto;
    }

    public Employee toEntity(EmployeeDTO dto) {
        if (dto == null) return null;

        Employee entity = new Employee();
        entity.setId(dto.getId());
        entity.setFullName(dto.getFullName());
        entity.setJob(dto.getJob());
        entity.setGender(Boolean.TRUE.equals(dto.getGender()));
        entity.setBirthDate(dto.getBirthDate());
        entity.setAvatarSrc(dto.getAvatarSrc());

        return entity;
    }

    public void updateEntityFromDTO(EmployeeDTO dto, Employee entity) {
        if (dto == null || entity == null) return;

        entity.setFullName(dto.getFullName());
        entity.setJob(dto.getJob());
        entity.setGender(Boolean.TRUE.equals(dto.getGender()));
        entity.setBirthDate(dto.getBirthDate());
        entity.setAvatarSrc(dto.getAvatarSrc());
    }
}