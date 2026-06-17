package com.appshopbanhang.admin.dto;

public record VnpayCallbackResponse(
        boolean verified,
        boolean success,
        Integer orderId,
        String txnRef,
        String responseCode,
        String transactionStatus,
        String message,
        OrderResponse order
) {
}
