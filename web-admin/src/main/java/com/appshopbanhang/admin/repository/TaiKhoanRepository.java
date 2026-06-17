package com.appshopbanhang.admin.repository;

import com.appshopbanhang.admin.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, String> {
    Optional<TaiKhoan> findByUsernameAndPassword(String username, String password);

    List<TaiKhoan> findByRoleOrderByUsernameAsc(String role);
}
