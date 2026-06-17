package com.appshopbanhang.admin.controller.admin;

import com.appshopbanhang.admin.dto.VnpayPaymentResponse;
import com.appshopbanhang.admin.service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminPaymentController {
    private final VnpayService vnpayService;

    public AdminPaymentController(VnpayService vnpayService) {
        this.vnpayService = vnpayService;
    }

    @PostMapping("/admin/orders/{id}/vnpay/create")
    public String createVnpayPayment(
            @PathVariable Integer id,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        VnpayPaymentResponse response = vnpayService.createPayment(id, null, null, "vn", clientIp(request));
        redirectAttributes.addFlashAttribute("message", "Da tao link VNPAY sandbox");
        return "redirect:" + response.paymentUrl();
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor;
        }
        return request.getRemoteAddr();
    }
}
