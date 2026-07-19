package com.produto.api.config.security.jwt;

import lombok.Builder;

import java.util.UUID;

@Builder
public record JWTUserData(
        UUID userId
) {
}
