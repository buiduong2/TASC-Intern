package com.backend.user.service;

import com.backend.user.dto.req.ChangePasswordReq;
import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.req.RefreshTokenReq;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.req.RevokeJwtReq;
import com.backend.user.dto.res.AuthRes;

public interface AuthService {

    AuthRes login(LoginReq loginReq);

    AuthRes register(RegisterReq registerReq);

    AuthRes refreshToken(RefreshTokenReq req);

    void changePassword(ChangePasswordReq req, long userId);

    void revoke(RevokeJwtReq req, long userId);
}
