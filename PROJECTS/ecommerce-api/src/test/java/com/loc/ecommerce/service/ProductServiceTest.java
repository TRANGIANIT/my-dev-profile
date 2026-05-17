package com.loc.ecommerce.service;

import com.loc.ecommerce.dto.ProductRequest;
import com.loc.ecommerce.dto.ProductResponse;
import com.loc.ecommerce.entity.Product;
import com.loc.ecommerce.exception.ResourceNotFoundException;
import com.loc.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void create_shouldSaveProductAndReturnResponse() {
        ProductRequest request = new ProductRequest(
                "Keyboard",
                "Mechanical keyboard",
                BigDecimal.valueOf(120.50),
                10
        );
        Product savedProduct = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = productService.create(request);

        assertThat(response.name()).isEqualTo("Keyboard");
        assertThat(response.price()).isEqualByComparingTo("120.50");
        assertThat(response.stockQuantity()).isEqualTo(10);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void findById_shouldThrowExceptionWhenProductDoesNotExist() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Product not found with id: 99");
    }
}
