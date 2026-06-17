package com.appshopbanhang.admin.repository;

import com.appshopbanhang.admin.entity.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DanhGiaRepository extends JpaRepository<DanhGia, Integer> {
    List<DanhGia> findByProductIdOrderByIdDesc(Integer productId);

    List<DanhGia> findAllByOrderByIdDesc();
}
