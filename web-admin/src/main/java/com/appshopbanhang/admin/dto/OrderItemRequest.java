package com.appshopbanhang.admin.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull Integer productId,
        @NotNull @Positive Integer quantity
) {
}
