package com.appshopbanhang.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "taikhoan")
public class TaiKhoan {
    @Id
    @Column(name = "tendn", length = 20, nullable = false)
    private String username;

    @Column(name = "matkhau", length = 100, nullable = false)
    private String password;

    @Column(name = "quyen", length = 50, nullable = false)
    private String role = "user";

    public TaiKhoan() {
    }

    public TaiKhoan(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
