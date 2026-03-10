package com.produto.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record WithdrawOrPutProductDTO(
        @NotNull(message = "A quantidade não pode ser nula")
        @PositiveOrZero(message = "A quantidade não pode ser igual ou menor que zero")
        Integer quantity
) {
}
