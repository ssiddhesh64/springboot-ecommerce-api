package com.ecomm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecomm.dto.OrderItemRequest;
import com.ecomm.dto.OrderRequest;
import com.ecomm.dto.OrderResponse;
import com.ecomm.entity.Order;
import com.ecomm.entity.OrderItem;
import com.ecomm.entity.Product;
import com.ecomm.entity.User;
import com.ecomm.exception.ResourceNotFoundException;
import com.ecomm.repository.OrderRepository;
import com.ecomm.repository.ProductRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void placeOrder_Success() {
        User user = User.builder().id(1L).name("Siddhesh").build();
        Product product = Product.builder()
                .id(101L)
                .name("MacBook Pro")
                .price(new BigDecimal("1999.99"))
                .stock(5)
                .active(true)
                .build();

        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(101L, 2)));

        when(productRepository.findById(101L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            OrderItem item = o.getItems().get(0);
            item.setId(1001L);
            return Order.builder()
                    .id(500L)
                    .user(o.getUser())
                    .createdAt(LocalDateTime.now())
                    .items(o.getItems())
                    .build();
        });

        OrderResponse response = orderService.placeOrder(user, request);

        assertNotNull(response);
        assertEquals(500L, response.id());
        assertEquals(1L, response.userId());
        assertEquals(1, response.items().size());
        assertEquals(new BigDecimal("3999.98"), response.totalPrice()); // 1999.99 * 2
        assertEquals(3, product.getStock()); // 5 - 2 = 3

        verify(productRepository, times(1)).findById(101L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void placeOrder_ProductNotFound_ThrowsException() {
        User user = User.builder().id(1L).name("Siddhesh").build();
        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(999L, 1)));

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder(user, request));
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void placeOrder_InsufficientStock_ThrowsException() {
        User user = User.builder().id(1L).name("Siddhesh").build();
        Product product = Product.builder()
                .id(101L)
                .name("MacBook Pro")
                .price(new BigDecimal("1999.99"))
                .stock(1)
                .active(true)
                .build();

        OrderRequest request = new OrderRequest(List.of(new OrderItemRequest(101L, 2)));

        when(productRepository.findById(101L)).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> orderService.placeOrder(user, request));
        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(orderRepository, never()).save(any(Order.class));
    }
}
