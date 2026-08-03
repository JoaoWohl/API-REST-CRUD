package com.produto.api.integration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.produto.api.dto.request.user.LoginRequestDTO;
import com.produto.api.dto.request.user.RegisterUserRequestDTO;
import com.produto.api.entity.DeleteUserToken;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.integration.BaseIntegrationTest;
import com.produto.api.repository.DeleteUserTokenRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Autowired
    private DeleteUserTokenRepository deleteUserTokenRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        deleteUserTokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User createUser(UserRole role) {
        User user = new User();
        user.setName("Test Name");
        user.setLogin("testlogin@example.com");
        user.setPassword(bCryptPasswordEncoder.encode("TestPassword"));
        user.setRole(role);

        return userRepository.save(user);
    }

    private String authenticateUser(User user) {
        return jwtTokenService.generateToken(user);
    }

    @Test
    void login_ShouldReturnOk() throws Exception {
        createUser(UserRole.USER);

        LoginRequestDTO request = new LoginRequestDTO("testlogin@example.com","TestPassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(cookie().httpOnly("JWT_TOKEN",true));
    }

    @Test
    void login_WhenLoginIsCorrectAndPasswordIsWrong_ShouldReturnUnauthorized() throws Exception {
        createUser(UserRole.USER);

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
        String userToken = authenticateUser(createUser(UserRole.USER));

        mockMvc.perform(post("/auth/logout")
                .cookie(new Cookie("JWT_TOKEN", userToken)))
                .andExpect(status().isOk());
    }

    @Test
    void logout_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteRequest_ShouldReturnOk() throws Exception {
        String userToken = authenticateUser(createUser(UserRole.USER));
        mockMvc.perform(post("/auth/delete-request")
                        .cookie(new Cookie("JWT_TOKEN", userToken)))
                .andExpect(status().isAccepted());
    }

    @Test
    void deleteRequest_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/auth/delete-request"))
                .andExpect(status().isForbidden());
    }

    @Test
    void confirmDeletion_ShouldReturnOk() throws Exception {
        User user = createUser(UserRole.USER);
        DeleteUserToken newToken = new DeleteUserToken();
        newToken.setUser(user);
        DeleteUserToken deleteUserToken = deleteUserTokenRepository.save(newToken);

        mockMvc.perform(post("/auth/confirm-deletion")
                .param("token", deleteUserToken.getToken().toString()))
                .andExpect(status().isOk());
    }

    @Test
    void confirmDeletion_ShouldReturnBadRequest_WhenUsedIsTrue() throws Exception {
        User user = createUser(UserRole.USER);
        DeleteUserToken newToken = new DeleteUserToken();
        newToken.setUser(user);
        newToken.setUsed(true);
        DeleteUserToken deleteUserToken = deleteUserTokenRepository.save(newToken);

        mockMvc.perform(post("/auth/confirm-deletion")
                .param("token", deleteUserToken.getToken().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmDeletion_ShouldReturnBadRequest_WhenExpiredToken() throws Exception {
        User user = createUser(UserRole.USER);
        DeleteUserToken newToken = new DeleteUserToken();
        newToken.setUser(user);
        newToken.setExpires_at(LocalDateTime.now().minusHours(1));
        DeleteUserToken deleteUserToken = deleteUserTokenRepository.save(newToken);

        mockMvc.perform(post("/auth/confirm-deletion")
                .param("token", deleteUserToken.getToken().toString()))
                .andExpect(status().isBadRequest());
    }
}
