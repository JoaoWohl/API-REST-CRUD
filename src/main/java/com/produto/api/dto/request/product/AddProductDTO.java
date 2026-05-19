package com.produto.api.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AddProductDTO(
        @Schema(example = "notebook")
        @NotNull(message = "O nome não pode ser nulo")
        String name,

        @Schema(example = "4500.00")
        @NotNull(message = "O preço não pode ser nulo")
        @PositiveOrZero(message = "O preço não pode ser negativo")
        BigDecimal price,

        @Schema(example = "10")
        @NotNull(message = "A quantidade não pode ser nula")
        @PositiveOrZero(message = "A quantidade não pode ser negativa")
        Integer quantity
) {}
