package com.produto.api.dto.request.product;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record WithdrawOrPutProductDTO(
        @Schema(example = "2")
        @NotNull(message = "A quantidade não pode ser nula")
        @PositiveOrZero(message = "A quantidade não pode ser igual ou menor que zero")
        Integer quantity
) {
}
