package com.appshopbanhang.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReviewRequest(
        @NotNull Integer productId,
        Integer orderDetailId,
        @NotBlank String username,
        @NotBlank String content,
        String star1,
        String star2,
        String star3,
        String star4,
        String star5
) {
}
