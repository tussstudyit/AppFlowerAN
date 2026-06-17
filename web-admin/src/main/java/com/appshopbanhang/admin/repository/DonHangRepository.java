package com.appshopbanhang.admin.repository;

import com.appshopbanhang.admin.entity.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonHangRepository extends JpaRepository<DonHang, Integer> {
    List<DonHang> findAllByOrderByIdDesc();

    List<DonHang> findByUsernameOrderByIdDesc(String username);

    List<DonHang> findByCustomerNameOrderByIdDesc(String customerName);

    Optional<DonHang> findByVnpTxnRef(String vnpTxnRef);
}
