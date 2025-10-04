package com.admin_bff.mapper;

import java.util.List;
import java.util.Map;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import com.admin_bff.dto.res.ProfileDTO;
import com.admin_bff.dto.res.UserAuthDTO;
import com.admin_bff.dto.res.UserSummaryDTO;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    List<UserSummaryDTO> toSummaryDTO(List<UserAuthDTO> auths, @Context Map<Long, ProfileDTO> profiles,
            @Context Map<Long, Long> orderCounts);

    UserSummaryDTO toSummaryDTO(UserAuthDTO auths, @Context Map<Long, ProfileDTO> profiles,
            @Context Map<Long, Long> orderCounts);

    @AfterMapping
    default void composeSummaryFields(
            UserAuthDTO source,
            @MappingTarget UserSummaryDTO target,
            @Context Map<Long, ProfileDTO> profiles,
            @Context Map<Long, Long> orderCounts) {
        updateProfile(target, getProfileDTO(source, profiles));
        updateOrder(target, getOrderCount(source, orderCounts));

    }

    @Mapping(target = "userId", ignore = true)
    void updateProfile(@MappingTarget UserSummaryDTO user, ProfileDTO profileDTO);

    void updateOrder(@MappingTarget UserSummaryDTO user, Long orderCount);

    default ProfileDTO getProfileDTO(UserAuthDTO auth, @Context Map<Long, ProfileDTO> profiles) {
        return profiles.getOrDefault(auth.getUserId(), new ProfileDTO());
    }

    default long getOrderCount(UserAuthDTO auth, @Context Map<Long, Long> orderCounts) {
        return orderCounts.getOrDefault(auth.getUserId(), 0L);
    }

}
