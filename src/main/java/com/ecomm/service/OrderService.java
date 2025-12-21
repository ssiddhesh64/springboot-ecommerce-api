package com.ecomm.service;

import com.ecomm.dto.OrderRequest;
import com.ecomm.entity.Order;
import com.ecomm.entity.OrderItem;
import com.ecomm.entity.Product;
import com.ecomm.entity.User;
import com.ecomm.repository.OrderRepository;
import com.ecomm.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order placeOrder(User user, OrderRequest request) {

        Order order = Order.builder()
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        List<OrderItem> items = request.items().stream().map(req -> {
            Product product = productRepository.findById(req.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < req.quantity()) {
                throw new RuntimeException("Insufficient stock");
            }

            product.setStock(product.getStock() - req.quantity());

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(req.quantity())
                    .build();
        }).toList();

        order.setItems(items);
        return orderRepository.save(order);
    }
}

