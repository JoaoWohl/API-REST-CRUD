package com.produto.api.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record RegisterUserResponseDTO(
        @Schema(example = "ExampleName")
        String name,
        @Schema(example = "ExampleEmail@example.com")
        String login
) {
}
