package com.authentication_service.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.authentication_service.dto.req.RegisterReq;
import com.authentication_service.dto.res.UserDTO;
import com.authentication_service.model.Role;
import com.authentication_service.model.SystemUser;
import com.authentication_service.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    UserDTO toDTO(User user);

    default List<String> toRoles(Collection<Role> roles) {
        return roles.stream().map(Role::getName).map(String::valueOf).toList();
    }

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "fullName", expression = "java( req.getLastName() + \" \"  + req.getFirstName()  )")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    SystemUser toSystemUser(RegisterReq req);
}
