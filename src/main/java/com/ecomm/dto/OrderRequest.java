package com.ecomm.dto;

import java.util.List;

public record OrderRequest(List<OrderItemRequest> items) {}
