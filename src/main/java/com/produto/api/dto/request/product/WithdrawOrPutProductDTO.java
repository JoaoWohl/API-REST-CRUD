package com.produto.api.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WithdrawOrPutProductDTO(
        @Schema(example = "2")
        @NotNull(message = "A quantidade não pode ser nula")
        @Positive(message = "A quantidade deve ser maior que zero")
        Integer quantity
) {
}
