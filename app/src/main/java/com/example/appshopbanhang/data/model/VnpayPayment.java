package com.example.appshopbanhang.data.model;

public class VnpayPayment {
    private final int orderId;
    private final String txnRef;
    private final float amount;
    private final String paymentStatus;
    private final String paymentUrl;
    private final String qrContent;
    private final String qrImageBase64;
    private final String qrMimeType;
    private final long expiresAt;
    private final String message;

    public VnpayPayment(
            int orderId,
            String txnRef,
            float amount,
            String paymentStatus,
            String paymentUrl,
            String qrContent,
            String qrImageBase64,
            String qrMimeType,
            long expiresAt,
            String message
    ) {
        this.orderId = orderId;
        this.txnRef = txnRef;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentUrl = paymentUrl;
        this.qrContent = qrContent;
        this.qrImageBase64 = qrImageBase64;
        this.qrMimeType = qrMimeType;
        this.expiresAt = expiresAt;
        this.message = message;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getTxnRef() {
        return txnRef;
    }

    public float getAmount() {
        return amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public String getQrContent() {
        return qrContent;
    }

    public String getQrImageBase64() {
        return qrImageBase64;
    }

    public String getQrMimeType() {
        return qrMimeType;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public String getMessage() {
        return message;
    }
}
