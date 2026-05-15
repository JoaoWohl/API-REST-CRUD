package com.produto.api.config.security.jwt;

import lombok.Builder;

@Builder
public record JWTUserData(
        String userId
) {
}
