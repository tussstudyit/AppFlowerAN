package com.appshopbanhang.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record OrderStatusRequest(
        @NotBlank String status
) {
}
