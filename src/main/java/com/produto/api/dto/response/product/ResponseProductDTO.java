package com.produto.api.dto.response.product;

import java.math.BigDecimal;

public record ResponseProductDTO(
        Long id,
        String name,
        BigDecimal price,
        Integer quantity
) {}
