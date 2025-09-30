package com.authentication_service.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuthUserService {
    UserDetails updateOrRegisterThirdPartyUser(OAuth2AuthenticationToken authentication, OAuth2User oAuth2User);
}
