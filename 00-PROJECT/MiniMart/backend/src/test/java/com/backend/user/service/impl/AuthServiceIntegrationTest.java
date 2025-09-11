package com.backend.user.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.lenient;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import com.backend.common.config.JpaConfig;
import com.backend.common.config.TestServiceConfig2;
import com.backend.user.dto.req.ChangePasswordReq;
import com.backend.user.dto.req.LoginReq;
import com.backend.user.dto.req.RegisterReq;
import com.backend.user.dto.res.AuthRes;
import com.backend.user.exception.UserInactiveException;
import com.backend.user.mapper.UserMapper;
import com.backend.user.model.Customer;
import com.backend.user.model.RoleName;
import com.backend.user.model.User;
import com.backend.user.model.UserStatus;
import com.backend.user.repository.CustomerRepository;
import com.backend.user.repository.JwtBlackListRepository;
import com.backend.user.repository.RoleRepository;
import com.backend.user.repository.TestUserRepository;
import com.backend.user.service.JwtService;
import com.backend.user.utils.JwtCodec;
import com.github.javafaker.Faker;

@AutoConfigureTestDatabase(replace = Replace.NONE)
@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Import({ TestServiceConfig2.class, JpaConfig.class })
public class AuthServiceIntegrationTest {

    private PasswordEncoder passwordEncoder;

    private UserMapper userMapper;

    @Autowired
    private TestUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtBlackListRepository jwtBlackListRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private JwtCodec jwtCodec;

    private JwtService jwtService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userMapper = Mappers.getMapper(UserMapper.class);
        jwtCodec = Mockito.mock(JwtCodec.class);
        lenient().doReturn("fakeToken").when(jwtCodec).build(anyMap(), any());

        jwtService = new JwtServiceImpl(jwtCodec, jwtBlackListRepository);
        authService = new AuthServiceImpl(passwordEncoder, userMapper, userRepository, roleRepository,
                customerRepository, jwtService);
    }

    private RegisterReq buildValidRegisterReq() {
        Faker faker = new Faker();

        RegisterReq registerReq = new RegisterReq();
        registerReq.setPassword("fakePassword");
        registerReq.setFirstName("FirstName");
        registerReq.setLastName("lastName");
        registerReq.setPhone("phone");
        registerReq.setConfirmPassword("fakePassword");

        String username;

        do {
            username = faker.name().username();

        } while (userRepository.existsByUsername(username));
        String email;
        do {
            email = "asd" + Math.random() + faker.internet().emailAddress();

        } while (userRepository.existsByEmail(email));
        return registerReq;
    }

    // Register
    @Test
    void register_withValidRequest_shouldPersistUserWithEncodedPasswordAndRoleCustomerAndCustomer() {
        RegisterReq registerReq = buildValidRegisterReq();

        AuthRes authRes = authService.register(registerReq);
        entityManager.flush();
        entityManager.clear();
        // Then
        long userId = authRes.getUser().getId();

        User user = userRepository.findById(userId).orElseThrow();

        Customer customer = customerRepository.findByUserId(userId).orElseThrow();

        // User-password
        assertThat(passwordEncoder.matches(registerReq.getPassword(), user.getPassword())).isTrue();
        assertThat(registerReq.getPassword()).isNotEqualTo(user.getPassword());

        // User, UserStatus
        assertThat(user.getStatus()).isEqualTo(UserStatus.ACTIVE);
        assertThat(user.getEmail()).isEqualTo(registerReq.getEmail());
        assertThat(user.getUsername()).isEqualTo(registerReq.getUsername());

        // Role -> customer
        assertThat(user.getRoles()).hasSize(1);
        assertThat(user.getRoles().get(0).getName()).isEqualTo(RoleName.CUSTOMER);

        // AUDIT
        assertThat(user.getAudit()).isNotNull();
        assertThat(user.getAudit().getCreatedAt()).isNotNull();
        assertThat(customer.getAudit().getCreatedAt()).isNotNull();

        // Customer , Customer.profile
        assertThat(customer).isNotNull();
        assertThat(customer.getProfile().getFirstName()).isEqualTo(registerReq.getFirstName());
        assertThat(customer.getProfile().getLastName()).isEqualTo(registerReq.getLastName());
        assertThat(customer.getProfile().getPhone()).isEqualTo(registerReq.getPhone());
    }

    @Test
    void register_withDuplicateUsername_shouldThrowConstraintViolationException() {
        User user = userRepository.findFirstByOrderByIdAsc().orElseThrow();

        RegisterReq req = buildValidRegisterReq();
        req.setUsername(user.getUsername());

        assertThrows(ConstraintViolationException.class, () -> {

            authService.register(req);
            entityManager.flush();

            return;
        });
    }

    @Test

    void register_withDuplicateEmail_shouldThrowConstraintViolationException() {
        User user = userRepository.findFirstByOrderByIdAsc().orElseThrow();

        RegisterReq req = buildValidRegisterReq();
        req.setEmail(user.getEmail());

        assertThrows(ConstraintViolationException.class, () -> {

            authService.register(req);
            entityManager.flush();

            return;
        });

    }

    // Login
    @Test
    void login_withValidUser_shouldReturnTokens() {

        assertThat(userRepository.existsByUsername("root")).isTrue();
        LoginReq req = new LoginReq();
        req.setUsername("root");
        req.setPassword("12345678");

        AuthRes authRes = authService.login(req);

        assertThat(authRes.getUser().getUsername()).isEqualTo("root");
    }

    @Test
    void login_withWrongPassword_shouldThrowBadCredentialsException() {
        assertThat(userRepository.existsByUsername("root")).isTrue();

        LoginReq req = new LoginReq();
        req.setUsername("root");
        req.setPassword("12345678" + "FAKE");

        assertThrows(BadCredentialsException.class, () -> authService.login(req));

    }

    @Test
    void login_withInactiveUser_shouldThrowUserInactiveException() {
        assertThat(userRepository.existsByUsername("inactive")).isTrue();
        LoginReq req = new LoginReq();
        req.setUsername("inactive");
        req.setPassword("12345678");

        assertThrows(UserInactiveException.class, () -> authService.login(req));
    }

    // Refresh Token

    // change-password

    @Test
    void saveUser_whenPasswordUpdated_shouldPersistNewPasswordHash() {
        User user = userRepository.findByUsername("root").orElseThrow();

        ChangePasswordReq req = new ChangePasswordReq();
        req.setOldPassword("12345678");

        req.setConfirmPassword("HelloWOrld");
        String password = "HelloWOrld";
        req.setPassword(password);

        entityManager.flush();
        entityManager.clear();

        authService.changePassword(req, user.getId());

        entityManager.flush();
        entityManager.clear();

        User updatedUser = userRepository.findById(user.getId()).orElseThrow();

        assertThat(updatedUser.getTokenVersion()).isEqualTo(user.getTokenVersion() + 1);
        assertThat(updatedUser.getPassword()).isNotEqualTo(user.getPassword());
        assertThat(passwordEncoder.matches(password, updatedUser.getPassword()));

    }

}
