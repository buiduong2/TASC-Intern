package com.authentication_service.service;

import com.authentication_service.dto.req.ChangePasswordReq;
import com.authentication_service.dto.req.RegisterReq;
import com.authentication_service.dto.res.UserDTO;

public interface AuthService {

    UserDTO getInfo(Long authId);

    UserDTO register(RegisterReq registerReq);

    void changePassword(ChangePasswordReq req, Long authId);

}
