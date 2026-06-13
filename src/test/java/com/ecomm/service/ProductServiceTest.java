package com.ecomm.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ecomm.dto.ProductRequest;
import com.ecomm.dto.ProductResponse;
import com.ecomm.entity.Product;
import com.ecomm.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void createProduct_Success() {
        ProductRequest request = new ProductRequest("iPhone", "Latest model", new BigDecimal("999.99"), 10);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product p = invocation.getArgument(0);
            return Product.builder()
                    .id(1L)
                    .name(p.getName())
                    .description(p.getDescription())
                    .price(p.getPrice())
                    .stock(p.getStock())
                    .active(p.isActive())
                    .build();
        });

        ProductResponse response = productService.createProduct(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("iPhone", response.name());
        assertEquals(new BigDecimal("999.99"), response.price());
        assertEquals(10, response.stock());
    }

    @Test
    void getAllProducts_FiltersInactive() {
        Product p1 = Product.builder()
                .id(1L)
                .name("Active Product")
                .price(BigDecimal.TEN)
                .stock(5)
                .active(true)
                .build();
        Product p2 = Product.builder()
                .id(2L)
                .name("Inactive Product")
                .price(BigDecimal.TEN)
                .stock(5)
                .active(false)
                .build();

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        List<ProductResponse> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals("Active Product", products.get(0).name());
    }
}
