package com.produto.api.dto.request.user;

import com.produto.api.entity.user.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequestDTO(
        @Schema(example = "ExampleName")
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @Schema(example = "ExampleEmail@gmail.com")
        @NotBlank(message = "Email é obrigatório")
        String login,

        @Schema(example = "ExamplePassword")
        @NotBlank(message = "Senha é obrigatório")
        String password,

        @Schema(example = "USER")
        UserRole role
) {
    public RegisterUserRequestDTO {
        if (role == null) role = UserRole.USER;
    }
}
