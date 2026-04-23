package com.produto.api.dto.request.user;

import com.produto.api.entity.user.UserRole;
import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequestDTO(
        @NotEmpty(message = "Nome é obrigatório") String name,
        @NotEmpty(message = "Email é obrigatório") String login,
        @NotEmpty(message = "Senha é obrigatório") String password,
        UserRole role
) {
    public RegisterUserRequestDTO {
        if (role == null) role = UserRole.USER;
    }
}
