package com.profile_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.profile_service.dto.res.ProfileDTO;
import com.profile_service.dto.res.ProfileInfo;
import com.profile_service.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<ProfileInfo> findInfoByUserId(Long userId);

    Optional<Profile> findByUserId(long userId);

    boolean existsByUserId(Long userId);

    List<ProfileDTO> findByIdIn(List<Long> ids);

}
