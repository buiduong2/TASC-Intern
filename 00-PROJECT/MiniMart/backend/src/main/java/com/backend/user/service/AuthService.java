package com.backend.user.service;

import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.req.RefreshTokenReq;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.res.AuthRes;

public interface AuthService {

    AuthRes login(LoginReq loginReq);

    AuthRes register(RegisterReq registerReq);

    AuthRes refreshToken(RefreshTokenReq req);
}
