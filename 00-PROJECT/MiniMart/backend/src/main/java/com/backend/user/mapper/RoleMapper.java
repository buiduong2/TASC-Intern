package com.backend.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.backend.user.dto.res.UserAdminDetailDTO.RoleDTO;
import com.backend.user.model.Role;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)

public interface RoleMapper {
    RoleDTO toRoleDTO(Role role);
}
