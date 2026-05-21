package com.produto.api.dto.request.user;

import com.produto.api.entity.user.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record RegisterUserRequestDTO(
        @Schema(example = "ExampleName")
        @NotEmpty(message = "Nome é obrigatório") String name,
        @Schema(example = "ExampleEmail@gmail.com")
        @NotEmpty(message = "Email é obrigatório") String login,
        @Schema(example = "ExamplePassword")
        @NotEmpty(message = "Senha é obrigatório") String password,
        @Schema(example = "USER")
        UserRole role
) {
    public RegisterUserRequestDTO {
        if (role == null) role = UserRole.USER;
    }
}
