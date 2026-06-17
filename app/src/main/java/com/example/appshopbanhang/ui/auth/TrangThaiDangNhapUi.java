package com.example.appshopbanhang.ui.auth;

public class TrangThaiDangNhapUi {
    private final boolean success;
    private final String username;
    private final String role;
    private final String message;

    private TrangThaiDangNhapUi(boolean success, String username, String role, String message) {
        this.success = success;
        this.username = username;
        this.role = role;
        this.message = message;
    }

    public static TrangThaiDangNhapUi success(String username, String role) {
        return new TrangThaiDangNhapUi(true, username, role, null);
    }

    public static TrangThaiDangNhapUi error(String message) {
        return new TrangThaiDangNhapUi(false, null, null, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getMessage() {
        return message;
    }
}

