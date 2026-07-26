package com.produto.api.security;

import lombok.Builder;

import java.util.UUID;

@Builder
public record JWTUserData(
        UUID userId
) {
}
