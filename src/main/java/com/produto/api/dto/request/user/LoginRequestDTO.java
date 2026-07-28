package com.produto.api.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @Schema(example = "ExampleEmail@example.com")
        @NotBlank(message = "Email é obrigatório")
        String login,

        @Schema(example = "ExamplePassword")
        @NotBlank(message = "Senha é obrigatório")
        String password
) {
}
