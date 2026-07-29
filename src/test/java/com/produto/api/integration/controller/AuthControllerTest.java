package com.produto.api.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.produto.api.dto.request.user.LoginRequestDTO;
import com.produto.api.dto.request.user.RegisterUserRequestDTO;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.integration.BaseIntegrationTest;
import com.produto.api.repository.UserRepository;
import com.produto.api.security.JwtTokenService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest extends BaseIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    JwtTokenService jwtTokenService;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    void login_ShouldReturnOk() throws Exception {
        User user = new User();
        user.setLogin("logintest@test.com");
        user.setPassword(bCryptPasswordEncoder.encode("testPassword"));
        user.setName("testName");
        user.setRole(UserRole.USER);
        userRepository.save(user);

        LoginRequestDTO request = new LoginRequestDTO("loginTest@test.com","testPassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().httpOnly("JWT_TOKEN",true));
    }

    @Test
    void login_WhenLoginIsCorrectAndPasswordIsWrong_ShouldReturnUnauthorized() throws Exception {
        User user = new User();
        user.setLogin("loginTest@test.com");
        user.setPassword(bCryptPasswordEncoder.encode("testPassword"));
        user.setName("testName");
        user.setRole(UserRole.USER);
        userRepository.save(user);

        LoginRequestDTO request = new LoginRequestDTO("loginTest@test.com","incorrectPassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WhenUserNotExist_ShouldReturnUnauthorizedError() throws Exception {
        LoginRequestDTO request = new LoginRequestDTO("loginTest@test.com","testPassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_ShouldReturnOk() throws Exception {
        RegisterUserRequestDTO request = new RegisterUserRequestDTO("testName","testEmail@test.com","testPassword",UserRole.USER);

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("testName"))
                .andExpect(jsonPath("$.login").value("testemail@test.com"));
    }

    @Test
    void register_ShouldReturnConflictError() throws Exception {
        User user = new User();
        user.setLogin("testemail@test.com");
        user.setPassword(bCryptPasswordEncoder.encode("testPassword"));
        user.setName("testName");
        user.setRole(UserRole.USER);
        userRepository.save(user);

        RegisterUserRequestDTO request = new RegisterUserRequestDTO("testName","testEmail@test.com","testPassword",UserRole.USER);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void adminRegister_ShouldReturnOk() throws Exception {

        RegisterUserRequestDTO request = new RegisterUserRequestDTO(
                "testName",
                "testEmail@test.com",
                "testPassword",
                UserRole.ADMIN
        );

        mockMvc.perform(post("/auth/admin/register")
                .with(user("usuario").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        List<User> users = userRepository.findAll();
        assertThat(users.getLast().getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void adminRegister_ShouldReturnConflictError() throws Exception {
        User user = new User();
        user.setName("testName");
        user.setLogin("testemail@test.com");
        user.setPassword(bCryptPasswordEncoder.encode("testPassword"));
        user.setRole(UserRole.ADMIN);
        userRepository.save(user);

        RegisterUserRequestDTO request = new RegisterUserRequestDTO(
                "testName",
                "testEmail@test.com",
                "testPassword",
                UserRole.ADMIN
        );

        mockMvc.perform(post("/auth/admin/register")
                        .with(user("usuario").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void adminRegister_ShouldReturnForbiddenError() throws Exception {
        RegisterUserRequestDTO request = new RegisterUserRequestDTO(
                "testName",
                "testEmail@test.com",
                "testPassword",
                UserRole.ADMIN
        );

        mockMvc.perform(post("/auth/admin/register")
                        .with(user("usuario").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void logout_ShouldReturnSuccess() throws Exception {
        User user = new User();
        user.setLogin("logintest@test.com");
        user.setPassword(bCryptPasswordEncoder.encode("testPassword"));
        user.setName("testName");
        user.setRole(UserRole.USER);
        userRepository.save(user);

        mockMvc.perform(post("/auth/logout")
                .cookie(new Cookie("JWT_TOKEN", jwtTokenService.generateToken(user))))
                .andExpect(status().isOk());
    }

    @Test
    void logout_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isForbidden());
    }
}
