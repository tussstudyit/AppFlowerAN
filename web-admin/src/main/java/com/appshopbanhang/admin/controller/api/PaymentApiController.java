package com.appshopbanhang.admin.controller.api;

import com.appshopbanhang.admin.dto.CreateVnpayOrderRequest;
import com.appshopbanhang.admin.dto.VnpayCallbackResponse;
import com.appshopbanhang.admin.dto.VnpayCreatePaymentRequest;
import com.appshopbanhang.admin.dto.VnpayIpnResponse;
import com.appshopbanhang.admin.dto.VnpayPaymentResponse;
import com.appshopbanhang.admin.service.VnpayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/vnpay")
public class PaymentApiController {
    private final VnpayService vnpayService;

    public PaymentApiController(VnpayService vnpayService) {
        this.vnpayService = vnpayService;
    }

    @PostMapping("/create-order")
    public VnpayPaymentResponse createOrderPayment(
            @Valid @RequestBody CreateVnpayOrderRequest request,
            HttpServletRequest servletRequest
    ) {
        return vnpayService.createOrderAndPayment(request, clientIp(servletRequest));
    }

    @PostMapping("/create")
    public VnpayPaymentResponse createPayment(
            @Valid @RequestBody VnpayCreatePaymentRequest request,
            HttpServletRequest servletRequest
    ) {
        return vnpayService.createPayment(
                request.orderId(),
                request.returnUrl(),
                request.bankCode(),
                request.locale(),
                clientIp(servletRequest)
        );
    }

    @GetMapping(value = "/return", produces = MediaType.TEXT_HTML_VALUE)
    public String returnUrl(@RequestParam Map<String, String> params) {
        VnpayCallbackResponse response = vnpayService.handleCallback(params);
        return vnpayService.buildReturnHtml(response);
    }

    @GetMapping("/ipn")
    public VnpayIpnResponse ipn(@RequestParam Map<String, String> params) {
        boolean alreadyPaid = vnpayService.isAlreadyPaid(params.get("vnp_TxnRef"));
        VnpayCallbackResponse response = vnpayService.handleCallback(params);
        if (!response.verified()) {
            return new VnpayIpnResponse("97", "Invalid Checksum");
        }
        if (response.orderId() == null) {
            return new VnpayIpnResponse("01", "Order not found");
        }
        if (response.message() != null && response.message().contains("So tien")) {
            return new VnpayIpnResponse("04", "Invalid amount");
        }
        if (alreadyPaid) {
            return new VnpayIpnResponse("02", "Order already confirmed");
        }
        return new VnpayIpnResponse("00", "Confirm Success");
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor;
        }
        return request.getRemoteAddr();
    }
}
