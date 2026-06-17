package com.appshopbanhang.admin.dto;

import jakarta.validation.constraints.NotNull;

public record VnpayCreatePaymentRequest(
        @NotNull Integer orderId,
        String returnUrl,
        String bankCode,
        String locale
) {
}
