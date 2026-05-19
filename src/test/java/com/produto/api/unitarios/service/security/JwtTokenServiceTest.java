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

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(jwtTokenService, "secret", "test-secret-key");
    }

    @Test
    void generateToken_ShouldReturnToken_WhenUserIsValid() {
        User user = new User("id-1", "TestName", "TestEmail@test.com", "TestPassword", UserRole.USER);

        String token = jwtTokenService.generateToken(user);

        assertNotNull(token);
    }

    @Test
    void validateToken_ShouldReturnUserData_WhenTokenIsValid() {
        User user = new User("id-1", "TestName", "TestEmail@test.com", "TestPassword", UserRole.USER);
        String token = jwtTokenService.generateToken(user);

        Optional<JWTUserData> result = jwtTokenService.validateToken(token);

        assertTrue(result.isPresent());
        assertEquals("id-1", result.get().userId());
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
                .withSubject("id-1")
                .withExpiresAt(Instant.now().plusSeconds(8000))
                .sign(wrongAlgorithm);

        Optional<JWTUserData> result = jwtTokenService.validateToken(token);

        assertTrue(result.isEmpty());
    }
}