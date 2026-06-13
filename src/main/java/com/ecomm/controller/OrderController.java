package com.ecomm.controller;

import com.ecomm.dto.OrderRequest;
import com.ecomm.dto.OrderResponse;
import com.ecomm.entity.User;
import com.ecomm.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse placeOrder(@AuthenticationPrincipal User user, @RequestBody OrderRequest request) {
        return orderService.placeOrder(user, request);
    }
}
