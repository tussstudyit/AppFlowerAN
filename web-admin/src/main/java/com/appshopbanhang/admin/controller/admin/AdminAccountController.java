package com.appshopbanhang.admin.controller.admin;

import com.appshopbanhang.admin.entity.TaiKhoan;
import com.appshopbanhang.admin.repository.TaiKhoanRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AdminAccountController {
    private final TaiKhoanRepository accountRepository;

    public AdminAccountController(TaiKhoanRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping("/admin/accounts")
    public String accounts(@RequestParam(required = false) String editUsername, Model model) {
        TaiKhoan editingAccount = editUsername == null
                ? new TaiKhoan()
                : accountRepository.findById(editUsername).orElse(new TaiKhoan());
        model.addAttribute("accounts", accountRepository.findAll());
        model.addAttribute("roles", List.of("admin", "user"));
        model.addAttribute("editingAccount", editingAccount);
        return "admin/accounts";
    }

    @PostMapping("/admin/accounts")
    public String saveAccount(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String role,
            RedirectAttributes redirectAttributes
    ) {
        accountRepository.save(new TaiKhoan(username.trim(), password, role));
        redirectAttributes.addFlashAttribute("message", "Da luu tai khoan");
        return "redirect:/admin/accounts";
    }

    @PostMapping("/admin/accounts/{username}/delete")
    public String deleteAccount(@PathVariable String username, RedirectAttributes redirectAttributes) {
        accountRepository.deleteById(username);
        redirectAttributes.addFlashAttribute("message", "Da xoa tai khoan");
        return "redirect:/admin/accounts";
    }
}
