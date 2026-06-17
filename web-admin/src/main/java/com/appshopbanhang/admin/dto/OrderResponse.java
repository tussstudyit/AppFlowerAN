package com.appshopbanhang.admin.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Integer id,
        String username,
        String customerName,
        String address,
        String phone,
        BigDecimal total,
        LocalDateTime orderDate,
        String status,
        String paymentMethod,
        String paymentStatus,
        String vnpTxnRef,
        String vnpTransactionNo,
        String vnpResponseCode,
        String vnpTransactionStatus,
        LocalDateTime vnpPayDate,
        String vnpBankCode,
        String vnpCardType,
        List<OrderItemResponse> items
) {
}
