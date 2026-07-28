package com.produto.api.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record UpdateProductDTO(
        @Schema(example = "Notebook")
        @NotBlank(message = "O nome deve ser preenchido")
        String name,

        @Schema(example = "5000")
        @Positive(message = "O preço deve ser maior que zero")
        @NotNull(message = "O preço não pode ser nulo")
        BigDecimal price,

        @Schema(example = "7")
        @Positive(message = "A quantidade deve ser maior que zero")
        @NotNull(message = "A quantidade não pode ser nula")
        Integer quantity
) {}
