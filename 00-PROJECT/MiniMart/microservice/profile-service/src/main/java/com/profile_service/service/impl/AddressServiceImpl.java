package com.profile_service.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.profile_service.dto.req.AddressCreateReq;
import com.profile_service.dto.res.AddressInfo;
import com.profile_service.exception.ErrorCode;
import com.profile_service.exception.GenericException;
import com.profile_service.mapper.AddressMapper;
import com.profile_service.model.Address;
import com.profile_service.model.Profile;
import com.profile_service.repository.AddressRepository;
import com.profile_service.repository.ProfileRepository;
import com.profile_service.service.AddressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepository repository;

    private final ProfileRepository profileRepository;

    private final AddressMapper mapper;

    @Override
    public Page<AddressInfo> findByProfileIdAndUserId(Long userId, Pageable pageable) {
        return repository.findByProfileIdAndProfileUserId(userId, pageable);
    }

    @Override
    public AddressInfo findByIdAndUserId(long id, Long userId) {
        return repository.findInfoByIdAndProfileUserId(id, userId)
                .orElseThrow(() -> new GenericException(ErrorCode.ADDRESS_NOT_FOUND, id));
    }

    @Transactional
    @Override
    public AddressInfo create(long profileId, AddressCreateReq req, Long userId) {

        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new GenericException(ErrorCode.PROFILE_NOT_FOUND, profileId));

        Address address = mapper.toEntity(req);

        address.setProfile(profile);

        address = repository.save(address);

        return mapper.toDTO(address);
    }

    @Transactional
    @Override
    public AddressInfo update(long id, AddressCreateReq req, Long userId) {
        Address address = repository.findByIdAndProfileUserId(id, userId)
                .orElseThrow(() -> new GenericException(ErrorCode.ADDRESS_NOT_FOUND, id));

        mapper.updateEntity(address, req);
        address = repository.save(address);
        return mapper.toDTO(address);
    }

    @Transactional
    @Override
    public void delete(long id, Long userId) {
        int rowAffected = repository.deleteByIdAndProfileUserId(id, userId);
        if (rowAffected != 1) {
            throw new GenericException(ErrorCode.ADDRESS_NOT_FOUND, id);
        }
    }

}
