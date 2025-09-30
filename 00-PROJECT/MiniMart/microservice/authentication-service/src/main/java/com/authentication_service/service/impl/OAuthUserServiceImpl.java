package com.authentication_service.service.impl;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.authentication_service.enums.OAUthProvider;
import com.authentication_service.model.OAuthUser;
import com.authentication_service.repository.OAuthUserRepository;
import com.authentication_service.security.OAuthUserDetails;
import com.authentication_service.service.OAuthUserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OAuthUserServiceImpl implements OAuthUserService {

    private final OAuthUserRepository repository;

    @Override
    public UserDetails updateOrRegisterThirdPartyUser(OAuth2AuthenticationToken authentication, OAuth2User oAuth2User) {
        OAuthUser userEntity = null;
        if (authentication.getAuthorizedClientRegistrationId().equals("github")) {
            userEntity = updateOrCreateGithubUser(oAuth2User);
        } else if (authentication.getAuthorizedClientRegistrationId().equals("google")) {
            userEntity = updateOrCreateGoogleUser(oAuth2User);
        }

        if (userEntity != null) {
            repository.saveAndFlush(userEntity);
            return new OAuthUserDetails(userEntity);
        }

        throw new UnsupportedOperationException(
                "Unimplemented provider " + authentication.getAuthorizedClientRegistrationId());
    }

    private OAuthUser findOrCreateByUsernameAndProider(OAUthProvider provider, String providerUserId) {
        return repository.findByProviderUserIdAndProvider(providerUserId, provider)
                .orElseGet(() -> {
                    OAuthUser newUser = new OAuthUser();
                    newUser.setProvider(provider);
                    newUser.setProviderUserId(providerUserId);
                    return newUser;
                });
    }

    private OAuthUser updateOrCreateGithubUser(OAuth2User githubUser) {
        OAUthProvider provider = OAUthProvider.GITHUB;
        Integer userId = githubUser.getAttribute("id");

        OAuthUser user = findOrCreateByUsernameAndProider(provider, String.valueOf(userId));

        String githubUsername = githubUser.getAttribute("login");

        String fullName = githubUser.getAttribute("name");
        String email = githubUser.getAttribute("email");
        String avatarUrl = githubUser.getAttribute("avatar_url");

        user.setAvatarUrl(avatarUrl);
        user.setEmail(email);
        user.setFullName(fullName == null ? githubUsername : fullName);
        user.setUsername(githubUsername);

        return user;
    }

    private OAuthUser updateOrCreateGoogleUser(OAuth2User googleUser) {
        OAUthProvider provider = OAUthProvider.GOOGLE;
        String userId = googleUser.getAttribute("sub");
        OAuthUser user = findOrCreateByUsernameAndProider(provider, userId);

        String name = googleUser.getAttribute("name");
        String email = googleUser.getAttribute("email");
        String pictureUrl = googleUser.getAttribute("picture");

        user.setFullName(name);
        user.setEmail(email);
        user.setAvatarUrl(pictureUrl);
        user.setUsername(email);
        return user;
    }
}