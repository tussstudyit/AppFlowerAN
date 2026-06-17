package com.example.appshopbanhang.data.model;

public class KetQuaDangNhap {
    private final boolean success;
    private final String username;
    private final String role;
    private final String message;

    private KetQuaDangNhap(boolean success, String username, String role, String message) {
        this.success = success;
        this.username = username;
        this.role = role;
        this.message = message;
    }

    public static KetQuaDangNhap success(String username, String role) {
        return new KetQuaDangNhap(true, username, role, null);
    }

    public static KetQuaDangNhap error(String message) {
        return new KetQuaDangNhap(false, null, null, message);
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

