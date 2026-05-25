package com.produto.api.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateProductDTO(
        @Schema(example = "Notebook")
        String name,

        @Schema(example = "5000")
        @PositiveOrZero(message = "O preço não pode ser negativo")
        BigDecimal price,

        @Schema(example = "7")
        @PositiveOrZero(message = "A quantidade não pode ser negativa")
        Integer quantity
) {}
