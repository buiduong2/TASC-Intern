package com.backend.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.req.RefreshTokenReq;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.res.AuthRes;
import com.backend.user.exception.UserInactiveException;
import com.backend.user.mapper.UserMapper;
import com.backend.user.model.Customer;
import com.backend.user.model.Role;
import com.backend.user.model.RoleName;
import com.backend.user.model.User;
import com.backend.user.model.UserStatus;
import com.backend.user.repository.CustomerRepository;
import com.backend.user.repository.RoleRepository;
import com.backend.user.repository.UserRepository;
import com.backend.user.service.JwtService;
import com.backend.user.utils.JwtClaims;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;

@ExtendWith(MockitoExtension.class)
public class AuthServiceUnitTest {

    @Spy
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Spy
    private UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    AuthServiceImpl service;

    LoginReq loginReq;

    User user;

    RegisterReq registerReq;

    RefreshTokenReq refreshTokenReq;

    private final String password = "fakepassword";
    private final String username = "fakeusername";
    private final String email = "fakeEmail";

    private final String fakeAccessToken = "FakeAccessToken";
    private final String fakeRefreshToken = "FakeRefreshToken";

    @BeforeEach
    void setup() {
        loginReq = new LoginReq();
        loginReq.setPassword(password);
        loginReq.setUsername(username);

        setupRegisterReq();
        setupUser();
        setupRefreshTokenReq();

        Claims claims = new DefaultClaims();
        claims.put(JwtClaims.UID, user.getId());

        lenient().doReturn(claims).when(jwtService).validateRefreshToken(eq(fakeRefreshToken), any());
        lenient().doReturn(Optional.of(user)).when(userRepository).findByUsername(username);
        lenient().doReturn(fakeAccessToken).when(jwtService).generateAccessToken(any());
        lenient().doReturn(fakeRefreshToken).when(jwtService).generateRefreshToken(any());

        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.CUSTOMER);
        lenient().doReturn(Optional.of(role)).when(roleRepository).findByName(eq(RoleName.CUSTOMER));

    }

    RegisterReq setupRegisterReq() {
        registerReq = new RegisterReq();
        registerReq.setEmail(email);
        registerReq.setUsername(username);
        registerReq.setConfirmPassword(password);
        registerReq.setPassword(password);
        registerReq.setPhone(email);

        return registerReq;
    }

    RefreshTokenReq setupRefreshTokenReq() {
        refreshTokenReq = new RefreshTokenReq();
        refreshTokenReq.setRefreshToken(fakeRefreshToken);

        return refreshTokenReq;
    }

    User setupUser() {
        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setPassword(passwordEncoder.encode(password));
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.CUSTOMER);
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName(RoleName.STAFF);
        user.setRoles(List.of(role, role2));
        user.setStatus(UserStatus.ACTIVE);
        user.setTokenVersion(100);
        return user;

    }

    // Login
    @Test
    void login_givenValidCredentials_shouldReturnAuthResWithTokens() {
        AuthRes authRes = service.login(loginReq);

        verify(userRepository, times(1)).findByUsername(anyString());
        verify(jwtService, times(1)).generateAccessToken(eq(user));
        verify(jwtService, times(1)).generateRefreshToken(eq(user));

        assertThat(authRes).isNotNull();
        assertThat(authRes.getAccessToken()).isEqualTo(fakeAccessToken);
        assertThat(authRes.getRefreshToken()).isEqualTo(fakeRefreshToken);
        assertThat(authRes.getUser().getId()).isEqualTo(user.getId());
        assertThat(authRes.getUser().getFullName()).isEqualTo(user.getFullName());
        assertThat(authRes.getUser().getUsername()).isEqualTo(user.getUsername());
        assertThat(authRes.getUser().getRoles())
                .isEqualTo(user.getRoles().stream().map(Role::getName).map(String::valueOf).toList());
    }

    @Test
    void login_givenWrongUsername_shouldThrowBadCredentialsException() {
        doReturn(Optional.empty()).when(userRepository).findByUsername(anyString());
        assertThrows(BadCredentialsException.class, () -> service.login(loginReq));
        verify(userRepository, times(1)).findByUsername(anyString());

        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    void login_givenWrongPassword_shouldThrowBadCredentialsException() {
        loginReq.setPassword(password + "RANDOM");

        assertThrows(BadCredentialsException.class, () -> service.login(loginReq));
        verify(passwordEncoder, times(1)).matches(eq(loginReq.getPassword()), eq(user.getPassword()));

        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Test
    void login_givenInactiveUser_shouldThrowUserInactiveException() {
        user.setStatus(UserStatus.INACTIVE);
        doReturn(Optional.of(user)).when(userRepository).findByUsername(anyString());
        assertThrows(UserInactiveException.class, () -> service.login(loginReq));
        verify(userRepository, times(1)).findByUsername(eq(username));

        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtService, never()).generateAccessToken(any());
        verify(jwtService, never()).generateRefreshToken(any());
    }

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<Customer> customerCaptor;

    // Register
    @Test
    void register_givenValidRequest_shouldSaveUserWithEncodedPasswordAndRoleCustomer() {
        long fakeId = 100L;
        doAnswer(invok -> {
            User user = invok.getArgument(0);
            user.setId(fakeId);
            return user;
        }).when(userRepository).save(any());

        AuthRes authRes = service.register(registerReq);

        verify(roleRepository, times(1)).findByName(eq(RoleName.CUSTOMER));
        verify(jwtService, times(1)).generateAccessToken(any());
        verify(jwtService, times(1)).generateRefreshToken(any());
        verify(userRepository, times(1)).save(userCaptor.capture());
        verify(customerRepository, times(1)).save(customerCaptor.capture());

        assertThat(authRes).isNotNull();
        assertThat(authRes.getAccessToken()).isEqualTo(fakeAccessToken);
        assertThat(authRes.getRefreshToken()).isEqualTo(fakeRefreshToken);
        assertThat(authRes.getUser().getId()).isEqualTo(fakeId);
        assertThat(authRes.getUser().getUsername()).isNotNull();
        assertThat(authRes.getUser().getRoles()).hasSize(1);
        assertThat(authRes.getUser().getRoles().get(0)).isEqualTo(RoleName.CUSTOMER.toString());

        User user = userCaptor.getValue();

        Customer customer = customerCaptor.getValue();

        assertThat(user).isNotNull();
        assertThat(customer).isNotNull();
        assertThat(customer.getUser()).isEqualTo(user);
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getRoles().get(0).getName()).isEqualTo(RoleName.CUSTOMER);

    }

    // Refresh Token
    @Test
    void refreshToken_givenValidRefreshToken_shouldReturnNewAccessToken() {
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());

        AuthRes authRes = service.refreshToken(refreshTokenReq);

        verify(jwtService, never()).generateRefreshToken(any());

        assertThat(authRes).isNotNull();
        assertThat(authRes.getRefreshToken()).isEqualTo(fakeRefreshToken);
        assertThat(authRes.getAccessToken()).isEqualTo(fakeAccessToken);
        assertThat(authRes.getUser()).isNotNull();

    }

    @Test
    void refreshToken_givenInactiveUser_shouldThrowUserInactiveException() {

        user.setStatus(UserStatus.INACTIVE);
        doReturn(Optional.of(user)).when(userRepository).findById(anyLong());

        assertThrows(UserInactiveException.class, () -> service.refreshToken(refreshTokenReq));

        verify(jwtService, never()).generateAccessToken(any());

    }

    @Test
    void refreshToken_givenUserNotFound_shouldThrowBadCredentialsException() {
        doReturn(Optional.empty()).when(userRepository).findById(anyLong());

        assertThrows(BadCredentialsException.class, () -> service.refreshToken(refreshTokenReq));

    }


    // ChangePassword
    void changePassword_withCorrectOldPassword_shouldUpdatePasswordAndIncrementTokenVersion() {

    }

    void changePassword_withIncorrectOldPassword_shouldThrowBadCredentialsException() {

    }

    


}
