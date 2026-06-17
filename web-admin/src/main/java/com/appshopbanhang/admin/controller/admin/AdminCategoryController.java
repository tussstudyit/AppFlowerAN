package com.appshopbanhang.admin.controller.admin;

import com.appshopbanhang.admin.entity.NhomSanPham;
import com.appshopbanhang.admin.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AdminCategoryController {
    private final CatalogService catalogService;

    public AdminCategoryController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/admin/categories")
    public String categories(@RequestParam(required = false) Integer editId, Model model) {
        NhomSanPham editingCategory = editId == null ? new NhomSanPham() : catalogService.getCategory(editId);
        model.addAttribute("categories", catalogService.getCategoryEntities());
        model.addAttribute("editingCategory", editingCategory);
        return "admin/categories";
    }

    @PostMapping("/admin/categories")
    public String saveCategory(
            @RequestParam(required = false) Integer id,
            @RequestParam String name,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirectAttributes
    ) throws IOException {
        catalogService.saveCategory(id, name, image);
        redirectAttributes.addFlashAttribute("message", "Da luu danh muc");
        return "redirect:/admin/categories";
    }

    @PostMapping("/admin/categories/{id}/delete")
    public String deleteCategory(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        catalogService.deleteCategory(id);
        redirectAttributes.addFlashAttribute("message", "Da xoa danh muc");
        return "redirect:/admin/categories";
    }
}
