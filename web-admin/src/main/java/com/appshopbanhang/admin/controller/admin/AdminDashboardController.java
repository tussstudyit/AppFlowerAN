package com.appshopbanhang.admin.controller.admin;

import com.appshopbanhang.admin.repository.DanhGiaRepository;
import com.appshopbanhang.admin.repository.DonHangRepository;
import com.appshopbanhang.admin.repository.NhomSanPhamRepository;
import com.appshopbanhang.admin.repository.SanPhamRepository;
import com.appshopbanhang.admin.repository.TaiKhoanRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import static com.appshopbanhang.admin.config.AdminSession.ADMIN_USERNAME;

@Controller
public class AdminDashboardController {
    private final TaiKhoanRepository accountRepository;
    private final NhomSanPhamRepository categoryRepository;
    private final SanPhamRepository productRepository;
    private final DonHangRepository orderRepository;
    private final DanhGiaRepository reviewRepository;

    public AdminDashboardController(
            TaiKhoanRepository accountRepository,
            NhomSanPhamRepository categoryRepository,
            SanPhamRepository productRepository,
            DonHangRepository orderRepository,
            DanhGiaRepository reviewRepository
    ) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
    }

    @GetMapping("/admin")
    public String dashboard(Model model, HttpSession session) {
        model.addAttribute("adminUsername", session.getAttribute(ADMIN_USERNAME));
        model.addAttribute("accountCount", accountRepository.count());
        model.addAttribute("categoryCount", categoryRepository.count());
        model.addAttribute("productCount", productRepository.count());
        model.addAttribute("orderCount", orderRepository.count());
        model.addAttribute("reviewCount", reviewRepository.count());
        model.addAttribute("recentOrders", orderRepository.findAllByOrderByIdDesc().stream().limit(5).toList());
        return "admin/dashboard";
    }
}
