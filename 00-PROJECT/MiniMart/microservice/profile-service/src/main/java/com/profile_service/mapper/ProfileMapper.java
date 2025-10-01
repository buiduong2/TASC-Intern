package com.profile_service.mapper;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import com.profile_service.dto.req.ProfileCreateReq;
import com.profile_service.dto.req.ProfileUpdateReq;
import com.profile_service.dto.res.ProfileInfo;
import com.profile_service.model.Profile;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, uses = AddressMapper.class, collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED)
public interface ProfileMapper {

    ProfileInfo toDTO(Profile profile);

    @ToEntity
    @Mapping(target = "userId", ignore = true)
    Profile toEntity(ProfileCreateReq req);

    @ToEntity
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "addresses", ignore = true)
    void update(@MappingTarget Profile target, ProfileUpdateReq req);
}