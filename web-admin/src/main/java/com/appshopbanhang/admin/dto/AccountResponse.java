package com.appshopbanhang.admin.dto;

public record AccountResponse(
        String username,
        String password,
        String role
) {
}
