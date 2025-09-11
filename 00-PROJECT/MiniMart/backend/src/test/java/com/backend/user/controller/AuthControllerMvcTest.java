package com.backend.user.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.backend.common.config.JpaConfig;
import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.res.AuthRes;
import com.backend.user.mapper.UserMapper;
import com.backend.user.model.Role;
import com.backend.user.model.RoleName;
import com.backend.user.model.User;
import com.backend.user.model.UserStatus;
import com.backend.user.repository.CustomerRepository;
import com.backend.user.repository.RoleRepository;
import com.backend.user.repository.UserRepository;
import com.backend.user.service.JwtService;
import com.backend.user.service.impl.AuthServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AuthController.class, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = JpaConfig.class))
@AutoConfigureMockMvc(addFilters = false)
@Import({ AuthServiceImpl.class })
public class AuthControllerMvcTest {

    private static final String API_AUTH_LOGIN = "/api/auth/login";

    @Autowired
    private MockMvc mvc;

    @MockitoSpyBean
    private AuthServiceImpl authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private RoleRepository repository;

    @MockitoBean
    private CustomerRepository customerRepository;

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
        authRes.setUser(new AuthRes.UserDTO());

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
                .andExpect(jsonPath("$.refreshToken").isString())
                .andExpect(jsonPath("$.accessToken").isString())
                .andExpect(jsonPath("$.user").isMap());

        verify(authService, times(1)).login(any());

    };

    @Test
    void login_givenWrongPassword_shouldReturn401() throws Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(req);
        doReturn(Optional.of(user)).when(userRepository).findByUsername(req.getUsername());
        doReturn(false).when(passwordEncoder).matches(anyString(), anyString());

        mvc.perform(
                post(API_AUTH_LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
        ;

        verify(authService, times(1)).login(any());
        verify(userRepository, times(1)).findByUsername(req.getUsername());
        verify(passwordEncoder, times(1)).matches(eq(req.getPassword()), anyString());
    };

    @Test
    void login_givenNonExistingUsername_shouldReturn401() throws Exception {
        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(req);
        doReturn(Optional.empty()).when(userRepository).findByUsername(req.getUsername());
        mvc.perform(
                post(API_AUTH_LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(authService, times(1)).login(any());
        verify(userRepository, times(1)).findByUsername(req.getUsername());
        verify(passwordEncoder, never()).matches(eq(req.getPassword()), anyString());
    };

    @Test
    void login_givenInactiveUser_shouldReturn403() throws Exception {
        user.setStatus(UserStatus.INACTIVE);

        ObjectMapper om = new ObjectMapper();
        String json = om.writeValueAsString(req);
        doReturn(Optional.of(user)).when(userRepository).findByUsername(req.getUsername());
        doReturn(true).when(passwordEncoder).matches(req.getPassword(), user.getPassword());
        mvc.perform(
                post(API_AUTH_LOGIN)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        verify(authService, times(1)).login(any());
        verify(userRepository, times(1)).findByUsername(req.getUsername());
        verify(passwordEncoder, times(1)).matches(eq(req.getPassword()), anyString());
    };

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
