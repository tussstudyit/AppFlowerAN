package com.appshopbanhang.admin.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Integer id,
        String name,
        BigDecimal price,
        String description,
        String note,
        Integer stock,
        Integer categoryId,
        String categoryName,
        String imageUrl
) {
}
