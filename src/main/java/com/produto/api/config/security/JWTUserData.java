package com.produto.api.config.security;

import lombok.Builder;

@Builder
public record JWTUserData(
        String userId
) {
}
