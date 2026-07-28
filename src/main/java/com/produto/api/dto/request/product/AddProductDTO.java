package com.produto.api.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record AddProductDTO(
        @Schema(example = "notebook")
        @NotBlank(message = "O nome não pode estar em branco")
        String name,

        @Schema(example = "4500.00")
        @NotNull(message = "O preço não pode ser nulo")
        @Positive(message = "O preço deve ser maior que zero")
        BigDecimal price,

        @Schema(example = "10")
        @NotNull(message = "A quantidade não pode ser nula")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantity
) {}
