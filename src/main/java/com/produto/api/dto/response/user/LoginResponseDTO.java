package com.produto.api.dto.response.user;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDTO(
        @Schema(example = "ExampleToken12314123")
        String token
) {
}
