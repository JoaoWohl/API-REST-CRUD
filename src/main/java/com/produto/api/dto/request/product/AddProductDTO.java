package com.produto.api.dto.request.product;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record AddProductDTO(
        @NotNull(message = "O nome não pode ser nulo")
        String name,

        @NotNull(message = "O preço não pode ser nulo")
        @PositiveOrZero(message = "O preço não pode ser negativo")
        BigDecimal price,

        @NotNull(message = "A quantidade não pode ser nula")
        @PositiveOrZero(message = "A quantidade não pode ser negativa")
        Integer quantity
) {}
