package com.appshopbanhang.admin.repository;

import com.appshopbanhang.admin.entity.NhomSanPham;
import com.appshopbanhang.admin.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {
    List<SanPham> findAllByOrderByIdDesc();

    List<SanPham> findByNameContainingIgnoreCaseOrderByIdDesc(String keyword);

    List<SanPham> findByCategoryOrderByIdDesc(NhomSanPham category);

    List<SanPham> findByCategoryAndNameContainingIgnoreCaseOrderByIdDesc(NhomSanPham category, String keyword);
}
