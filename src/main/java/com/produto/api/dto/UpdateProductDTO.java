package com.produto.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateProductDTO(
        String name,

        @PositiveOrZero(message = "O preço não pode ser negativo")
        BigDecimal price,

        @PositiveOrZero(message = "A quantidade não pode ser negativa")
        Integer quantity
) {}
