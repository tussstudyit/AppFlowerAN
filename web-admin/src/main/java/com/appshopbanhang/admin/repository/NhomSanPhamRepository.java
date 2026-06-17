package com.appshopbanhang.admin.repository;

import com.appshopbanhang.admin.entity.NhomSanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NhomSanPhamRepository extends JpaRepository<NhomSanPham, Integer> {
    List<NhomSanPham> findAllByOrderByIdAsc();
}
