package com.produto.api.dto.request.user;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequestDTO(
        @NotEmpty(message = "Email é obrigatório") String login,
        @NotEmpty(message = "Senha é obrigatório") String password
) {
}
