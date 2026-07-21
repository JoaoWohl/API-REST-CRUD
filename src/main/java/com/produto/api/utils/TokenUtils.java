package com.produto.api.utils;

import com.produto.api.config.security.jwt.JWTUserData;
import com.produto.api.service.security.JwtTokenService;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TokenUtils {
    @Autowired
    JwtTokenService jwtTokenService;

    public String getToken(String authHeader) {
        if (Strings.isNotEmpty(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    public UUID getUUID(String authHeader) {
        JWTUserData jwtData = jwtTokenService.validateToken(getToken(authHeader)).orElseThrow(() -> new RuntimeException("User Not Found"));
        return jwtData.userId();
    }
}
