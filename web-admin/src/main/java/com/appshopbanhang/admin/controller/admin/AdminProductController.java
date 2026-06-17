package com.appshopbanhang.admin.controller.admin;

import com.appshopbanhang.admin.entity.SanPham;
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
import java.math.BigDecimal;

@Controller
public class AdminProductController {
    private final CatalogService catalogService;

    public AdminProductController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/admin/products")
    public String products(@RequestParam(required = false) Integer editId, Model model) {
        SanPham editingProduct = editId == null ? new SanPham() : catalogService.getProduct(editId);
        model.addAttribute("products", catalogService.getProductEntities());
        model.addAttribute("categories", catalogService.getCategoryEntities());
        model.addAttribute("editingProduct", editingProduct);
        return "admin/products";
    }

    @PostMapping("/admin/products")
    public String saveProduct(
            @RequestParam(required = false) Integer id,
            @RequestParam String name,
            @RequestParam BigDecimal price,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String note,
            @RequestParam Integer stock,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) MultipartFile image,
            RedirectAttributes redirectAttributes
    ) throws IOException {
        catalogService.saveProduct(id, name, price, description, note, stock, categoryId, image);
        redirectAttributes.addFlashAttribute("message", "Da luu san pham");
        return "redirect:/admin/products";
    }

    @PostMapping("/admin/products/{id}/delete")
    public String deleteProduct(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        catalogService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("message", "Da xoa san pham");
        return "redirect:/admin/products";
    }
}
