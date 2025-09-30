package com.authentication_service.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ConfigurationProperties("custom.security.oauth2.client")
public class OAuth2ClientProperties {

    private Map<String, ClientRegistrationProperties> registered;

    @Getter
    @Setter
    @ToString
    public static class ClientRegistrationProperties {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
        private String postLogoutRedirectUri;
        private String scope;
    }
}