package com.loc.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loc.ecommerce.entity.Product;
import com.loc.ecommerce.repository.OrderRepository;
import com.loc.ecommerce.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldReturnCreatedProduct() throws Exception {
        ProductApiRequest request = new ProductApiRequest(
                "Keyboard",
                "Mechanical keyboard",
                BigDecimal.valueOf(120.50),
                10
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.description").value("Mechanical keyboard"))
                .andExpect(jsonPath("$.price").value(120.50))
                .andExpect(jsonPath("$.stockQuantity").value(10));

        assertThat(productRepository.findAll()).hasSize(1);
    }

    @Test
    void findAll_shouldReturnProducts() throws Exception {
        productRepository.save(new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 10));
        productRepository.save(new Product("Mouse", "Wireless mouse", BigDecimal.valueOf(45.00), 20));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Keyboard"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));
    }

    @Test
    void search_shouldReturnPagedFilteredProducts() throws Exception {
        productRepository.save(new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 10));
        productRepository.save(new Product("Mouse", "Wireless mouse", BigDecimal.valueOf(45.00), 0));
        productRepository.save(new Product("Monitor", "Wide display", BigDecimal.valueOf(250.00), 5));

        mockMvc.perform(get("/api/products/search")
                        .param("keyword", "key")
                        .param("minPrice", "100")
                        .param("inStock", "true")
                        .param("page", "0")
                        .param("size", "5")
                        .param("sort", "price,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name").value("Keyboard"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    void findById_shouldReturnProduct() throws Exception {
        Product product = productRepository.save(
                new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 10)
        );

        mockMvc.perform(get("/api/products/{id}", product.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Keyboard"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_shouldReturnUpdatedProduct() throws Exception {
        Product product = productRepository.save(
                new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 10)
        );
        ProductApiRequest request = new ProductApiRequest(
                "Gaming Keyboard",
                "RGB mechanical keyboard",
                BigDecimal.valueOf(150.00),
                5
        );

        mockMvc.perform(put("/api/products/{id}", product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.name").value("Gaming Keyboard"))
                .andExpect(jsonPath("$.description").value("RGB mechanical keyboard"))
                .andExpect(jsonPath("$.price").value(150.00))
                .andExpect(jsonPath("$.stockQuantity").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_shouldReturnNoContentAndRemoveProduct() throws Exception {
        Product product = productRepository.save(
                new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 10)
        );

        mockMvc.perform(delete("/api/products/{id}", product.getId()))
                .andExpect(status().isNoContent());

        assertThat(productRepository.existsById(product.getId())).isFalse();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadImage_shouldReturnProductWithImageUrl() throws Exception {
        Product product = productRepository.save(
                new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 10)
        );
        MockMultipartFile image = new MockMultipartFile(
                "file",
                "keyboard.png",
                "image/png",
                "fake-image".getBytes()
        );

        mockMvc.perform(multipart("/api/products/{id}/image", product.getId()).file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(product.getId()))
                .andExpect(jsonPath("$.imageUrl").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        ProductApiRequest request = new ProductApiRequest("", "Invalid product", BigDecimal.ZERO, -1);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Request validation failed"))
                .andExpect(jsonPath("$.validationErrors.name").value("Product name is required"))
                .andExpect(jsonPath("$.validationErrors.price").value("Price must be greater than 0"))
                .andExpect(jsonPath("$.validationErrors.stockQuantity")
                        .value("Stock quantity must be zero or greater"));
    }

    @Test
    void createProduct_shouldReturnUnauthorizedWithoutAuthentication() throws Exception {
        ProductApiRequest request = new ProductApiRequest(
                "Keyboard",
                "Mechanical keyboard",
                BigDecimal.valueOf(120.50),
                10
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void findById_shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/products/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }

    private record ProductApiRequest(
            String name,
            String description,
            BigDecimal price,
            Integer stockQuantity
    ) {
    }
}
