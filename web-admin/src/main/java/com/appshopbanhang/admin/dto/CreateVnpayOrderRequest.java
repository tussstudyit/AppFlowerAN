package com.appshopbanhang.admin.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateVnpayOrderRequest(
        String username,
        @NotBlank String customerName,
        @NotBlank String address,
        @NotBlank String phone,
        @Valid @NotEmpty List<OrderItemRequest> items,
        String returnUrl,
        String bankCode,
        String locale
) {
    public CreateOrderRequest toCreateOrderRequest() {
        return new CreateOrderRequest(username, customerName, address, phone, items);
    }
}
