package com.appshopbanhang.admin.controller.api;

import com.appshopbanhang.admin.dto.CreateOrderRequest;
import com.appshopbanhang.admin.dto.OrderResponse;
import com.appshopbanhang.admin.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {
    private final OrderService orderService;

    public OrderApiController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> orders(@RequestParam String username) {
        return orderService.getOrdersByUsername(username);
    }

    @GetMapping("/{id}")
    public OrderResponse order(@PathVariable Integer id) {
        return orderService.toOrderResponse(orderService.getOrderEntity(id));
    }

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }
}
