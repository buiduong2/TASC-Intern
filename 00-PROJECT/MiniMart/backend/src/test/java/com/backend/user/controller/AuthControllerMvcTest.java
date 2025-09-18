package com.backend.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.apache.hc.core5.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.backend.common.config.JpaConfig;
import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.res.AuthRes;
import com.backend.user.dto.res.UserDTO;
import com.backend.user.exception.UserInactiveException;
import com.backend.user.model.Role;
import com.backend.user.model.RoleName;
import com.backend.user.model.User;
import com.backend.user.model.UserStatus;
import com.backend.user.security.JwtAuthenticationFilter;
import com.backend.user.service.AuthService;
import com.backend.user.service.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JpaConfig.class))
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerMvcTest {

    private static final String API_AUTH_LOGIN = "/api/auth/login";

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter filter;

    private LoginReq req;

    private AuthRes authRes;

    private User user;

    @BeforeEach
    void setup() {

        req = new LoginReq();
        req.setPassword("12345678");
        req.setUsername("root");

        authRes = new AuthRes();
        authRes.setAccessToken("FAKE ACCESS");
        authRes.setRefreshToken("FAKE REFRESH");
        authRes.setUser(new UserDTO());

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword("Fake hashed password");
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.CUSTOMER);
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName(RoleName.STAFF);
        user.setRoles(List.of(role, role2));
        user.setStatus(UserStatus.ACTIVE);
        user.setTokenVersion(100);
    }

    @Test
    void login_givenValidCredentials_shouldReturn200AndTokens() throws JsonProcessingException, Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(req);

        doReturn(authRes).when(authService).login(any());

        mvc.perform(
                post(API_AUTH_LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.refreshToken").value(authRes.getRefreshToken()))
                .andExpect(jsonPath("$.accessToken").value(authRes.getAccessToken()))
                .andExpect(jsonPath("$.user").value(authRes.getUser()));

        verify(authService, times(1)).login(any());

    };

    @Test
    void login_givenWrongPassword_shouldReturn401() throws Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(req);
        doThrow(new BadCredentialsException("random  message. messages were fixed")).when(authService).login(any());

        mvc.perform(
                post(API_AUTH_LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpStatus.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("AUTH_INVALID"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.timestamp").isString());
        verify(authService, times(1)).login(any());

    };

    @Test
    void login_givenNonExistingUsername_shouldReturn401() throws Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(req);
        doThrow(new BadCredentialsException("random  message. messages were fixed")).when(authService).login(any());

        mvc.perform(
                post(API_AUTH_LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(HttpStatus.SC_UNAUTHORIZED))
                .andExpect(jsonPath("$.error").value("AUTH_INVALID"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.timestamp").isString());
        verify(authService, times(1)).login(any());

    };

    @Test
    void login_givenInactiveUser_shouldReturn403() throws Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(req);
        doThrow(new UserInactiveException("Mesage dynamic userinactive")).when(authService).login(any());
        mvc.perform(
                post(API_AUTH_LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(HttpStatus.SC_FORBIDDEN))
                .andExpect(jsonPath("$.error").value("USER_INACTIVE"))
                .andExpect(jsonPath("$.message").value("Mesage dynamic userinactive"))
                .andExpect(jsonPath("$.timestamp").isString());
        verify(authService, times(1)).login(any());
    }

    @Test
    void login_givenInvalidRequestBody_shouldReturn400() throws Exception {
        String json = """
                    {}
                """;
        mvc.perform(
                post(API_AUTH_LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(HttpStatus.SC_BAD_REQUEST))
                .andExpect(jsonPath("$.error").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Input validation Failed"))
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.errors").isArray());
        verify(authService, never()).login(any());
    }

    // Register
    @Test
    void register_givenValidRequest_shouldReturn201AndTokens() {

    }

    @Test
    void register_givenDuplicateEmail_shouldReturn409() {

    }

    @Test
    void register_givenDuplicateUsername_shouldReturn409() {

    }

    @Test
    void register_givenMissingFields_shouldReturn400() {

    }

    // Register
    @Test
    void refreshToken_givenValidRefreshToken_shouldReturn200AndNewAccessToken() {

    }

    @Test
    void refreshToken_givenInvalidRefreshToken_shouldReturn401() {

    }

    @Test
    void refreshToken_givenExpiredRefreshToken_shouldReturn401() {

    }

    @Test
    void refreshToken_givenInactiveUser_shouldReturn403() {

    }

    @Test
    void refreshToken_givenUserNotFound_shouldReturn401() {

    }

    // change-password
    void changePassword_withValidRequest_shouldReturn200AndForceLogout() {

    }

    void changePassword_withInvalidOldPassword_shouldReturn401() {

    }

    void changePassword_withMismatchedConfirmation_shouldReturn400() {

    }

    void changePassword_withoutAuthentication_shouldReturn401() {

    }

    void changePassword_withWeakNewPassword_shouldReturn400() {

    }
}
