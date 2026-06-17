package com.appshopbanhang.admin.controller.admin;

import com.appshopbanhang.admin.config.AdminSession;
import com.appshopbanhang.admin.dto.AuthLoginResponse;
import com.appshopbanhang.admin.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminAuthController {
    private final AuthService authService;

    public AdminAuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping({"/", "/admin/login"})
    public String loginForm(HttpSession session) {
        if (session.getAttribute(AdminSession.ADMIN_USERNAME) != null) {
            return "redirect:/admin";
        }
        return "admin/login";
    }

    @PostMapping("/admin/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        AuthLoginResponse response = authService.login(username, password);
        if (response.success() && "admin".equals(response.role())) {
            session.setAttribute(AdminSession.ADMIN_USERNAME, response.username());
            return "redirect:/admin";
        }

        model.addAttribute("error", "Tai khoan khong co quyen admin hoac thong tin dang nhap sai");
        return "admin/login";
    }

    @PostMapping("/admin/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }
}
