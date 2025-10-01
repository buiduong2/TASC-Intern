package com.profile_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.profile_service.dto.req.AddressCreateReq;
import com.profile_service.dto.res.AddressInfo;

public interface AddressService {

    Page<AddressInfo> findByProfileIdAndUserId(Long userId, Pageable pageable);

    AddressInfo findByIdAndUserId(long id, Long userId);

    AddressInfo create(long profileId, AddressCreateReq req, Long userId);

    AddressInfo update(long id, AddressCreateReq req, Long userId);

    void delete(long id, Long userId);

}
