package com.appshopbanhang.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record AccountRequest(
        @NotBlank String username,
        @NotBlank String password,
        @NotBlank String role
) {
}
