package com.produto.api.unitarios.service.security;

import com.produto.api.security.JWTUserData;
import com.produto.api.security.JwtTokenService;
import com.produto.api.security.TokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenUtilsTest {

    @InjectMocks
    private TokenUtils tokenUtils;

    private String validAuthHeader;
    private JWTUserData validJwtUserData;
    private static final UUID USER_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        validAuthHeader = "Bearer validToken";
        validJwtUserData = new JWTUserData(USER_ID);
    }

}
