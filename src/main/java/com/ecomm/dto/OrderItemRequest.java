package com.ecomm.dto;

public record OrderItemRequest(
        Long productId,
        Integer quantity
) {}
