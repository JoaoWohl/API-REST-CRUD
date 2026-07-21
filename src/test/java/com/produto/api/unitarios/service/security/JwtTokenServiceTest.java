package com.produto.api.unitarios.service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.produto.api.config.security.jwt.JWTUserData;
import com.produto.api.entity.user.User;
import com.produto.api.entity.user.UserRole;
import com.produto.api.service.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenService jwtTokenService;

    private User validUser;
    private static final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setup() {
        validUser = new User(USER_ID, "User Name", "testlogin@example.com", "TestPassword",UserRole.USER);
        ReflectionTestUtils.setField(jwtTokenService, "secret", "test-secret-key");
    }

    @Test
    void generateToken_ShouldReturnToken_WhenUserIsValid() {
        String token = jwtTokenService.generateToken(validUser);
        assertNotNull(token);
    }

    @Test
    void validateToken_ShouldReturnUserData_WhenTokenIsValid() {
        String token = jwtTokenService.generateToken(validUser);

        Optional<JWTUserData> result = jwtTokenService.validateToken(token);

        assertTrue(result.isPresent());
        assertEquals(USER_ID, result.get().userId());
    }

    @Test
    void validateToken_ShouldReturnEmpty_WhenTokenIsMalformed() {
        Optional<JWTUserData> result = jwtTokenService.validateToken("token-invalido");

        assertTrue(result.isEmpty());
    }

    @Test
    void validateToken_ShouldReturnEmpty_WhenTokenHasWrongSecret() {
        // Gera com secret diferente
        Algorithm wrongAlgorithm = Algorithm.HMAC256("secret-errado");
        String token = JWT.create()
                .withSubject(USER_ID.toString())
                .withExpiresAt(Instant.now().plusSeconds(8000))
                .sign(wrongAlgorithm);

        Optional<JWTUserData> result = jwtTokenService.validateToken(token);

        assertTrue(result.isEmpty());
    }
}