package com.appshopbanhang.admin.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        Integer id,
        Integer productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        String imageUrl
) {
}
