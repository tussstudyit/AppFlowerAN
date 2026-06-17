package com.appshopbanhang.admin.service;

import com.appshopbanhang.admin.dto.AuthLoginResponse;
import com.appshopbanhang.admin.entity.TaiKhoan;
import com.appshopbanhang.admin.repository.TaiKhoanRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final TaiKhoanRepository accountRepository;

    public AuthService(TaiKhoanRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AuthLoginResponse login(String username, String password) {
        String normalizedUsername = username == null ? "" : username.trim();
        Optional<TaiKhoan> account = accountRepository.findByUsernameAndPassword(normalizedUsername, password);
        if (account.isEmpty()) {
            return new AuthLoginResponse(false, null, null, "Ten dang nhap hoac mat khau khong dung");
        }

        TaiKhoan user = account.get();
        return new AuthLoginResponse(true, user.getUsername(), user.getRole(), "Dang nhap thanh cong");
    }

    public boolean isAdmin(String username) {
        return accountRepository.findById(username)
                .map(TaiKhoan::getRole)
                .map("admin"::equals)
                .orElse(false);
    }
}
