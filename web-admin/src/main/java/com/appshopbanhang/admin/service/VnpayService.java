package com.appshopbanhang.admin.service;

import com.appshopbanhang.admin.config.VnpayProperties;
import com.appshopbanhang.admin.dto.CreateVnpayOrderRequest;
import com.appshopbanhang.admin.dto.VnpayCallbackResponse;
import com.appshopbanhang.admin.dto.VnpayPaymentResponse;
import com.appshopbanhang.admin.entity.DonHang;
import com.appshopbanhang.admin.repository.DonHangRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class VnpayService {
    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final int QR_IMAGE_SIZE = 720;

    private final VnpayProperties properties;
    private final DonHangRepository orderRepository;
    private final OrderService orderService;

    public VnpayService(VnpayProperties properties, DonHangRepository orderRepository, OrderService orderService) {
        this.properties = properties;
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @Transactional
    public VnpayPaymentResponse createOrderAndPayment(CreateVnpayOrderRequest request, String clientIp) {
        DonHang order = orderService.createVnpayOrder(request.toCreateOrderRequest());
        return createPayment(order.getId(), request.returnUrl(), request.bankCode(), request.locale(), clientIp);
    }

    @Transactional
    public VnpayPaymentResponse createPayment(Integer orderId, String returnUrl, String bankCode, String locale, String clientIp) {
        DonHang order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Khong tim thay don hang"));
        if (OrderService.PAYMENT_STATUS_PAID.equals(order.getPaymentStatus())) {
            throw new ResponseStatusException(BAD_REQUEST, "Don hang da thanh toan");
        }
        if (order.getTotal() == null || order.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "Tong tien don hang khong hop le");
        }

        String txnRef = order.getId() + "_" + System.currentTimeMillis();
        order.setPaymentMethod(OrderService.PAYMENT_METHOD_VNPAY);
        order.setPaymentStatus(OrderService.PAYMENT_STATUS_PENDING);
        order.setStatus(OrderService.WAITING_PAYMENT_STATUS);
        order.setVnpTxnRef(txnRef);
        order.setVnpResponseCode(null);
        order.setVnpTransactionStatus(null);
        order.setVnpTransactionNo(null);
        order.setVnpPayDate(null);
        order.setVnpBankCode(null);
        order.setVnpCardType(null);
        DonHang savedOrder = orderRepository.save(order);

        PaymentUrlResult paymentUrlResult = buildPaymentUrl(savedOrder, txnRef, returnUrl, bankCode, locale, clientIp);
        String qrImageBase64 = createQrImageBase64(paymentUrlResult.paymentUrl());
        return new VnpayPaymentResponse(
                savedOrder.getId(),
                txnRef,
                savedOrder.getTotal(),
                savedOrder.getPaymentStatus(),
                paymentUrlResult.paymentUrl(),
                paymentUrlResult.paymentUrl(),
                qrImageBase64,
                "image/png",
                paymentUrlResult.expiresAt(),
                orderService.toOrderResponse(savedOrder),
                "Tao URL thanh toan VNPAY thanh cong"
        );
    }

    @Transactional
    public VnpayCallbackResponse handleCallback(Map<String, String> params) {
        boolean verified = verifySecureHash(params);
        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        String transactionStatus = params.get("vnp_TransactionStatus");

        if (!verified) {
            return new VnpayCallbackResponse(false, false, null, txnRef, responseCode, transactionStatus, "Sai chu ky VNPAY", null);
        }

        DonHang order = findOrderByTxnRef(txnRef);
        if (order == null) {
            return new VnpayCallbackResponse(true, false, null, txnRef, responseCode, transactionStatus, "Khong tim thay don hang", null);
        }

        if (!isAmountMatched(order, params.get("vnp_Amount"))) {
            return new VnpayCallbackResponse(true, false, order.getId(), txnRef, responseCode, transactionStatus, "So tien thanh toan khong khop", orderService.toOrderResponse(order));
        }

        boolean success = "00".equals(responseCode) && "00".equals(transactionStatus);
        order.setPaymentMethod(OrderService.PAYMENT_METHOD_VNPAY);
        order.setPaymentStatus(success ? OrderService.PAYMENT_STATUS_PAID : OrderService.PAYMENT_STATUS_FAILED);
        order.setStatus(success ? OrderService.DEFAULT_STATUS : OrderService.WAITING_PAYMENT_STATUS);
        order.setVnpResponseCode(responseCode);
        order.setVnpTransactionStatus(transactionStatus);
        order.setVnpTransactionNo(params.get("vnp_TransactionNo"));
        order.setVnpBankCode(params.get("vnp_BankCode"));
        order.setVnpCardType(params.get("vnp_CardType"));
        order.setVnpPayDate(parseVnpayDate(params.get("vnp_PayDate")));
        DonHang savedOrder = orderRepository.save(order);

        return new VnpayCallbackResponse(
                true,
                success,
                savedOrder.getId(),
                txnRef,
                responseCode,
                transactionStatus,
                success ? "Thanh toan VNPAY thanh cong" : "Thanh toan VNPAY that bai",
                orderService.toOrderResponse(savedOrder)
        );
    }

    public String buildReturnHtml(VnpayCallbackResponse response) {
        String title = response.success() ? "Thanh toan thanh cong" : "Thanh toan chua thanh cong";
        String color = response.success() ? "#14735f" : "#b42318";
        return """
                <!doctype html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s</title>
                    <style>
                        body{font-family:Arial,sans-serif;background:#f5f7fb;color:#172033;margin:0;min-height:100vh;display:grid;place-items:center;padding:24px}
                        main{background:#fff;border:1px solid #d9e0ea;border-radius:8px;padding:28px;max-width:520px;width:100%%;box-shadow:0 18px 60px rgba(23,32,51,.08)}
                        h1{color:%s;margin-top:0}.muted{color:#667085}.code{background:#f8fafc;border-radius:6px;padding:10px;margin-top:12px}
                    </style>
                </head>
                <body>
                    <main>
                        <h1>%s</h1>
                        <p class="muted">%s</p>
                        <div class="code">Ma don: %s<br>Ma giao dich: %s<br>ResponseCode: %s</div>
                    </main>
                </body>
                </html>
                """.formatted(
                title,
                color,
                title,
                response.message(),
                response.orderId() == null ? "" : response.orderId(),
                response.txnRef() == null ? "" : response.txnRef(),
                response.responseCode() == null ? "" : response.responseCode()
        );
    }

    public boolean isAlreadyPaid(String txnRef) {
        return orderRepository.findByVnpTxnRef(txnRef)
                .map(DonHang::getPaymentStatus)
                .map(OrderService.PAYMENT_STATUS_PAID::equals)
                .orElse(false);
    }

    private PaymentUrlResult buildPaymentUrl(DonHang order, String txnRef, String returnUrl, String bankCode, String locale, String clientIp) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expireDate = now.plusMinutes(15);
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", properties.getVersion());
        params.put("vnp_Command", properties.getCommand());
        params.put("vnp_TmnCode", properties.getTmnCode());
        params.put("vnp_Amount", toVnpayAmount(order.getTotal()));
        params.put("vnp_CurrCode", properties.getCurrency());
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        params.put("vnp_OrderType", properties.getOrderType());
        params.put("vnp_Locale", StringUtils.hasText(locale) ? locale : properties.getLocale());
        params.put("vnp_ReturnUrl", StringUtils.hasText(returnUrl) ? returnUrl : properties.getReturnUrl());
        params.put("vnp_IpAddr", normalizeIp(clientIp));
        params.put("vnp_CreateDate", now.format(VNPAY_DATE_FORMAT));
        params.put("vnp_ExpireDate", expireDate.format(VNPAY_DATE_FORMAT));
        String selectedBankCode = StringUtils.hasText(bankCode) ? bankCode : properties.getDefaultBankCode();
        if (StringUtils.hasText(selectedBankCode)) {
            params.put("vnp_BankCode", selectedBankCode);
        }

        String hashData = buildHashData(params);
        String secureHash = hmacSha512(properties.getHashSecret(), hashData);
        String paymentUrl = properties.getPayUrl() + "?" + buildQuery(params) + "&vnp_SecureHash=" + secureHash;
        long expiresAt = expireDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        return new PaymentUrlResult(paymentUrl, expiresAt);
    }

    private boolean verifySecureHash(Map<String, String> params) {
        String secureHash = params.get("vnp_SecureHash");
        if (!StringUtils.hasText(secureHash)) {
            return false;
        }

        Map<String, String> data = new TreeMap<>();
        params.forEach((key, value) -> {
            if (!"vnp_SecureHash".equals(key) && !"vnp_SecureHashType".equals(key) && StringUtils.hasText(value)) {
                data.put(key, value);
            }
        });
        String calculatedHash = hmacSha512(properties.getHashSecret(), buildHashData(data));
        return calculatedHash.equalsIgnoreCase(secureHash);
    }

    private DonHang findOrderByTxnRef(String txnRef) {
        if (!StringUtils.hasText(txnRef)) {
            return null;
        }
        return orderRepository.findByVnpTxnRef(txnRef)
                .orElseGet(() -> fallbackFindOrderById(txnRef));
    }

    private DonHang fallbackFindOrderById(String txnRef) {
        String orderId = txnRef.contains("_") ? txnRef.substring(0, txnRef.indexOf('_')) : txnRef;
        try {
            return orderRepository.findById(Integer.parseInt(orderId)).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private boolean isAmountMatched(DonHang order, String vnpAmount) {
        try {
            return Long.parseLong(vnpAmount) == Long.parseLong(toVnpayAmount(order.getTotal()));
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private String toVnpayAmount(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .toPlainString();
    }

    private LocalDateTime parseVnpayDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return LocalDateTime.parse(value, VNPAY_DATE_FORMAT);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private String buildHashData(Map<String, String> params) {
        return new TreeMap<>(params).entrySet()
                .stream()
                .filter(entry -> StringUtils.hasText(entry.getValue()))
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");
    }

    private String buildQuery(Map<String, String> params) {
        return new TreeMap<>(params).entrySet()
                .stream()
                .filter(entry -> StringUtils.hasText(entry.getValue()))
                .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
                .reduce((left, right) -> left + "&" + right)
                .orElse("");
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private String hmacSha512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception ex) {
            throw new IllegalStateException("Khong the tao chu ky VNPAY", ex);
        }
    }

    private String createQrImageBase64(String content) {
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.MARGIN, 1);
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);

            BitMatrix matrix = new QRCodeWriter().encode(
                    content,
                    BarcodeFormat.QR_CODE,
                    QR_IMAGE_SIZE,
                    QR_IMAGE_SIZE,
                    hints
            );
            BufferedImage image = new BufferedImage(QR_IMAGE_SIZE, QR_IMAGE_SIZE, BufferedImage.TYPE_INT_RGB);
            int black = Color.BLACK.getRGB();
            int white = Color.WHITE.getRGB();
            for (int x = 0; x < QR_IMAGE_SIZE; x++) {
                for (int y = 0; y < QR_IMAGE_SIZE; y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? black : white);
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "png", outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (Exception ex) {
            throw new IllegalStateException("Khong the tao QR VNPAY", ex);
        }
    }

    private String normalizeIp(String clientIp) {
        if (!StringUtils.hasText(clientIp) || "0:0:0:0:0:0:0:1".equals(clientIp)) {
            return "127.0.0.1";
        }
        int commaIndex = clientIp.indexOf(',');
        return commaIndex >= 0 ? clientIp.substring(0, commaIndex).trim() : clientIp;
    }

    private record PaymentUrlResult(String paymentUrl, long expiresAt) {
    }
}
