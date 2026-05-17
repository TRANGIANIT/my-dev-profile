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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerIntegrationTest {
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
    @WithMockUser(roles = "USER")
    void createOrder_shouldReturnCreatedOrderAndDecreaseProductStock() throws Exception {
        Product product = productRepository.save(
                new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 10)
        );
        CreateOrderApiRequest request = new CreateOrderApiRequest(
                List.of(new OrderItemApiRequest(product.getId(), 2))
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(241.00))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productId").value(product.getId()))
                .andExpect(jsonPath("$.items[0].productName").value("Keyboard"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].unitPrice").value(120.50))
                .andExpect(jsonPath("$.items[0].lineTotal").value(241.00));

        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(8);
    }

    @Test
    @WithMockUser(roles = "USER")
    void findAll_shouldReturnOrders() throws Exception {
        Product product = productRepository.save(
                new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 10)
        );
        CreateOrderApiRequest request = new CreateOrderApiRequest(
                List.of(new OrderItemApiRequest(product.getId(), 1))
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[0].items[0].productName").value("Keyboard"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void findById_shouldReturnOrder() throws Exception {
        Product product = productRepository.save(
                new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 10)
        );
        CreateOrderApiRequest request = new CreateOrderApiRequest(
                List.of(new OrderItemApiRequest(product.getId(), 1))
        );
        String responseBody = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Long orderId = objectMapper.readTree(responseBody).get("id").asLong();

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("CREATED"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_shouldReturnBadRequestWhenItemsAreEmpty() throws Exception {
        CreateOrderApiRequest request = new CreateOrderApiRequest(List.of());

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.validationErrors.items").value("Order items are required"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_shouldReturnBadRequestWhenStockIsInsufficient() throws Exception {
        Product product = productRepository.save(
                new Product("Keyboard", "Mechanical keyboard", BigDecimal.valueOf(120.50), 1)
        );
        CreateOrderApiRequest request = new CreateOrderApiRequest(
                List.of(new OrderItemApiRequest(product.getId(), 2))
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient stock for product id: " + product.getId()));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createOrder_shouldReturnNotFoundWhenProductDoesNotExist() throws Exception {
        CreateOrderApiRequest request = new CreateOrderApiRequest(
                List.of(new OrderItemApiRequest(999L, 1))
        );

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void findById_shouldReturnNotFoundWhenOrderDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/orders/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order not found with id: 999"));
    }

    @Test
    void findAll_shouldReturnForbiddenWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }

    private record CreateOrderApiRequest(List<OrderItemApiRequest> items) {
    }

    private record OrderItemApiRequest(Long productId, Integer quantity) {
    }
}
