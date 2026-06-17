package com.appshopbanhang.admin.service;

import com.appshopbanhang.admin.dto.CreateOrderRequest;
import com.appshopbanhang.admin.dto.OrderItemRequest;
import com.appshopbanhang.admin.dto.OrderItemResponse;
import com.appshopbanhang.admin.dto.OrderResponse;
import com.appshopbanhang.admin.entity.ChiTietDonHang;
import com.appshopbanhang.admin.entity.DonHang;
import com.appshopbanhang.admin.entity.SanPham;
import com.appshopbanhang.admin.repository.DonHangRepository;
import com.appshopbanhang.admin.repository.SanPhamRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class OrderService {
    public static final String DEFAULT_STATUS = "Cho Xac Nhan";
    public static final String WAITING_PAYMENT_STATUS = "Cho Thanh Toan";
    public static final String PAYMENT_METHOD_COD = "COD";
    public static final String PAYMENT_METHOD_VNPAY = "VNPAY";
    public static final String PAYMENT_STATUS_UNPAID = "UNPAID";
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_PAID = "PAID";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
    public static final List<String> STATUS_OPTIONS = List.of(
            "Cho Thanh Toan",
            "Cho Xac Nhan",
            "Dang Chuan Bi Hang",
            "Da Giao Cho DVVC",
            "Da Giao Hang",
            "Da Huy"
    );

    private final DonHangRepository orderRepository;
    private final SanPhamRepository productRepository;

    public OrderService(DonHangRepository orderRepository, SanPhamRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<DonHang> getOrderEntities() {
        return orderRepository.findAllByOrderByIdDesc();
    }

    public DonHang getOrderEntity(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Khong tim thay don hang"));
    }

    public List<OrderResponse> getOrdersByUsername(String username) {
        String normalized = username == null ? "" : username.trim();
        List<DonHang> orders = orderRepository.findByUsernameOrderByIdDesc(normalized);
        if (orders.isEmpty()) {
            orders = orderRepository.findByCustomerNameOrderByIdDesc(normalized);
        }
        return orders.stream().map(this::toOrderResponse).toList();
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {
        return toOrderResponse(createOrderEntity(request, PAYMENT_METHOD_COD));
    }

    @Transactional
    public DonHang createVnpayOrder(CreateOrderRequest request) {
        return createOrderEntity(request, PAYMENT_METHOD_VNPAY);
    }

    private DonHang createOrderEntity(CreateOrderRequest request, String paymentMethod) {
        DonHang order = new DonHang();
        order.setUsername(StringUtils.hasText(request.username()) ? request.username().trim() : null);
        order.setCustomerName(request.customerName().trim());
        order.setAddress(request.address().trim());
        order.setPhone(request.phone().trim());
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentMethod(paymentMethod);
        if (PAYMENT_METHOD_VNPAY.equals(paymentMethod)) {
            order.setStatus(WAITING_PAYMENT_STATUS);
            order.setPaymentStatus(PAYMENT_STATUS_PENDING);
        } else {
            order.setStatus(DEFAULT_STATUS);
            order.setPaymentStatus(PAYMENT_STATUS_UNPAID);
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.items()) {
            SanPham product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Khong tim thay san pham: " + itemRequest.productId()));
            int quantity = itemRequest.quantity();
            int currentStock = product.getStock() == null ? 0 : product.getStock();
            if (currentStock < quantity) {
                throw new ResponseStatusException(BAD_REQUEST, "San pham khong du ton kho: " + product.getName());
            }

            BigDecimal price = product.getPrice() == null ? BigDecimal.ZERO : product.getPrice();
            total = total.add(price.multiply(BigDecimal.valueOf(quantity)));
            product.setStock(currentStock - quantity);

            ChiTietDonHang item = new ChiTietDonHang();
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(price);
            item.setImageUrl(product.getImageUrl());
            order.addItem(item);
        }

        order.setTotal(total);
        return orderRepository.save(order);
    }

    @Transactional
    public void updateStatus(Integer orderId, String status) {
        DonHang order = getOrderEntity(orderId);
        order.setStatus(status);
        orderRepository.save(order);
    }

    public OrderResponse toOrderResponse(DonHang order) {
        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(this::toOrderItemResponse)
                .toList();
        return new OrderResponse(
                order.getId(),
                order.getUsername(),
                order.getCustomerName(),
                order.getAddress(),
                order.getPhone(),
                order.getTotal(),
                order.getOrderDate(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getPaymentStatus(),
                order.getVnpTxnRef(),
                order.getVnpTransactionNo(),
                order.getVnpResponseCode(),
                order.getVnpTransactionStatus(),
                order.getVnpPayDate(),
                order.getVnpBankCode(),
                order.getVnpCardType(),
                items
        );
    }

    private OrderItemResponse toOrderItemResponse(ChiTietDonHang item) {
        SanPham product = item.getProduct();
        Integer productId = product == null ? null : product.getId();
        String productName = product == null ? null : product.getName();
        return new OrderItemResponse(
                item.getId(),
                productId,
                productName,
                item.getQuantity(),
                item.getPrice(),
                item.getImageUrl()
        );
    }
}
