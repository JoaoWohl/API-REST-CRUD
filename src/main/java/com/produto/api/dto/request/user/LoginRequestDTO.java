package com.produto.api.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(
        @Schema(example = "ExampleEmail@example.com")
        @NotEmpty(message = "Email é obrigatório") String login,
        @Schema(example = "ExamplePassword")
        @NotEmpty(message = "Senha é obrigatório") String password
) {
}
