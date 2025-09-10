package com.backend.user.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.backend.common.utils.ToEntity;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.res.AuthRes;
import com.backend.user.model.Role;
import com.backend.user.model.User;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @ToEntity
    @Mapping(target = "customer.profile", source = ".")
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "fullName", expression = "java( req.getLastName() + \" \"  + req.getFirstName()  )")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "tokenVersion", ignore = true)
    User toEntity(RegisterReq req);

    AuthRes.UserDTO toDTO(User user);

    default List<String> toRoles(List<Role> roles) {
        return roles.stream().map(Role::getName).map(String::valueOf).toList();
    }
}
