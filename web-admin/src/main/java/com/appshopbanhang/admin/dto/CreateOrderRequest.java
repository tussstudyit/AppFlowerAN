package com.appshopbanhang.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        String username,
        @NotBlank String customerName,
        @NotBlank String address,
        @NotBlank String phone,
        @Valid @NotEmpty List<OrderItemRequest> items
) {
}
