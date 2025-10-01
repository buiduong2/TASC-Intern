package com.profile_service.service;

import com.profile_service.dto.req.ProfileCreateReq;
import com.profile_service.dto.req.ProfileUpdateReq;
import com.profile_service.dto.res.ProfileInfo;

public interface ProfileService {

    ProfileInfo findByIdAndUserId(Long userId);

    ProfileInfo create(ProfileCreateReq req, Long userId);

    ProfileInfo update(ProfileUpdateReq req, Long userId);

}
