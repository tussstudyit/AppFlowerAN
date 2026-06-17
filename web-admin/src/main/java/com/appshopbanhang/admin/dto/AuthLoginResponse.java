package com.appshopbanhang.admin.dto;

public record AuthLoginResponse(
        boolean success,
        String username,
        String role,
        String message
) {
}
