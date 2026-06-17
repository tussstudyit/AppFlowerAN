package com.appshopbanhang.admin.dto;

import java.math.BigDecimal;

public record VnpayPaymentResponse(
        Integer orderId,
        String txnRef,
        BigDecimal amount,
        String paymentStatus,
        String paymentUrl,
        String qrContent,
        String qrImageBase64,
        String qrMimeType,
        Long expiresAt,
        OrderResponse order,
        String message
) {
}
