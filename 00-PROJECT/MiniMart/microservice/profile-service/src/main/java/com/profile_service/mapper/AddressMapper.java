package com.profile_service.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.profile_service.dto.req.AddressCreateReq;
import com.profile_service.dto.res.AddressInfo;
import com.profile_service.model.Address;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {

    @Mapping(target = "profileId", source = "profile.id")
    AddressInfo toDTO(Address address);

    @ToEntity
    @Mapping(target = "profile", ignore = true)
    Address toEntity(AddressCreateReq req);

    @InheritConfiguration(name = "toEntity")
    void updateEntity(@MappingTarget Address target, AddressCreateReq req);
}
