package com.produto.api.config.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.produto.api.entity.user.User;
import org.springframework.beans.factory.annotation.Value;

import java.time.Instant;
import java.util.Optional;

public class TokenConfig {
    @Value("${JWT_SECRET}")
    private String secret;

    public String generateToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.create()
                .withSubject(user.getId())
                .withExpiresAt(Instant.now().plusSeconds(8000))
                .withIssuedAt(Instant.now())
                .sign(algorithm);
    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decode = JWT.require(algorithm).build().verify(token);
            return Optional.of(JWTUserData.builder()
                    .userId(decode.getSubject())
                    .build());
        }catch (JWTVerificationException ex) {
            return Optional.empty();
        }
    }
}
