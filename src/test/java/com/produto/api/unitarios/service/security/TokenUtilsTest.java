package com.produto.api.unitarios.service.security;


import com.produto.api.security.TokenUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class TokenUtilsTest {

    private TokenUtils tokenUtils;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        tokenUtils = new TokenUtils();
        request = mock(HttpServletRequest.class);
    }

    @Test
    void getTokenByCookie_ShouldReturnToken_WhenJwtCookieExists() {
        Cookie[] cookies = {
                new Cookie("OTHER_COOKIE", "123"),
                new Cookie("JWT_TOKEN", "my-token")
        };

        when(request.getCookies()).thenReturn(cookies);

        String token = tokenUtils.getTokenByCookie(request);

        assertThat(token).isEqualTo("my-token");
    }

    @Test
    void getTokenByCookie_ShouldReturnNull_WhenJwtCookieDoesNotExist() {
        Cookie[] cookies = {
                new Cookie("SESSION", "abc"),
                new Cookie("USER", "joao")
        };

        when(request.getCookies()).thenReturn(cookies);

        String token = tokenUtils.getTokenByCookie(request);

        assertThat(token).isNull();
    }

    @Test
    void getTokenByCookie_ShouldReturnNull_WhenRequestHasNoCookies() {
        when(request.getCookies()).thenReturn(null);

        String token = tokenUtils.getTokenByCookie(request);

        assertThat(token).isNull();
    }
}
