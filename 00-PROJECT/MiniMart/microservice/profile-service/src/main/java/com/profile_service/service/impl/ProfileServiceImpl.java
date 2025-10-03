package com.profile_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.common.exception.GenericException;
import com.profile_service.dto.req.ProfileCreateReq;
import com.profile_service.dto.req.ProfileUpdateReq;
import com.profile_service.dto.res.ProfileInfo;
import com.profile_service.exception.ErrorCode;
import com.profile_service.mapper.ProfileMapper;
import com.profile_service.model.Profile;
import com.profile_service.repository.ProfileRepository;
import com.profile_service.service.ProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository repository;

    private final ProfileMapper mapper;

    @Override
    public ProfileInfo findByIdAndUserId(Long userId) {
        return repository.findInfoByUserId(userId)
                .orElseThrow(() -> new GenericException(ErrorCode.PROFILE_NOT_FOUND, userId));
    }

    @Transactional
    @Override
    public ProfileInfo create(ProfileCreateReq req, Long userId) {
        if (repository.existsByUserId(userId)) {
            throw new GenericException(ErrorCode.PROFILE_EXISTED);
        }
        Profile profile = mapper.toEntity(req);

        profile.setUserId(userId);

        profile = repository.save(profile);

        return mapper.toDTO(profile);
    }

    @Transactional
    @Override
    public ProfileInfo update(ProfileUpdateReq req, Long userId) {

        Profile profile = repository.findByUserId(userId)
                .orElseThrow(() -> new GenericException(ErrorCode.PROFILE_NOT_FOUND, userId));

        mapper.update(profile, req);
        profile = repository.save(profile);
        return mapper.toDTO(profile);
    }

}
