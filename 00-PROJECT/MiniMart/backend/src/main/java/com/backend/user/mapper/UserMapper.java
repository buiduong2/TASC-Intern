package com.backend.user.mapper;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.backend.common.utils.ToEntity;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.req.UpdateUserAdminReq;
import com.backend.user.dto.res.UserAdminDTO;
import com.backend.user.dto.res.UserDTO;
import com.backend.user.model.Customer;
import com.backend.user.model.Role;
import com.backend.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @ToEntity

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "fullName", expression = "java( req.getLastName() + \" \"  + req.getFirstName()  )")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    User toEntity(RegisterReq req);

    @ToEntity
    @Mapping(target = "profile", source = ".")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    Customer toCustomerEntity(RegisterReq req);

    UserDTO toDTO(User user);

    default List<String> toRoles(Collection<Role> roles) {
        return roles.stream().map(Role::getName).map(String::valueOf).toList();
    }

    @Mapping(target = "createdAt", source = "audit.createdAt")
    UserAdminDTO toAdminDTO(User user);

    @ToEntity
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    void update(@MappingTarget User user, UpdateUserAdminReq req);

}
