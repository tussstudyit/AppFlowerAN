package com.appshopbanhang.admin.controller.admin;

import com.appshopbanhang.admin.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminOrderController {
    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/admin/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getOrderEntities());
        model.addAttribute("statusOptions", OrderService.STATUS_OPTIONS);
        return "admin/orders";
    }

    @GetMapping("/admin/orders/{id}")
    public String detail(@PathVariable Integer id, Model model) {
        model.addAttribute("order", orderService.getOrderEntity(id));
        model.addAttribute("statusOptions", OrderService.STATUS_OPTIONS);
        return "admin/order-detail";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateStatus(
            @PathVariable Integer id,
            @RequestParam String status,
            RedirectAttributes redirectAttributes
    ) {
        orderService.updateStatus(id, status);
        redirectAttributes.addFlashAttribute("message", "Da cap nhat trang thai don hang");
        return "redirect:/admin/orders/" + id;
    }
}
