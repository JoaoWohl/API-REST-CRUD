package com.produto.api.dto.response.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record ResponseProductDTO(
        @Schema(example = "1")
        UUID id,
        @Schema(example = "Notebook")
        String name,
        @Schema(example = "4500")
        BigDecimal price,
        @Schema(example = "10")
        Integer quantity
) {}
