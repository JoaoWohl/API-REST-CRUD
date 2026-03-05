package com.produto.api.dto;

import java.math.BigDecimal;

public record ResponseProductDTO(
        Long id,
        String name,
        BigDecimal price,
        Integer quantity
) {}
