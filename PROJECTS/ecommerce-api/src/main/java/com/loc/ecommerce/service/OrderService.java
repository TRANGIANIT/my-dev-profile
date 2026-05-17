package com.loc.ecommerce.service;

import com.loc.ecommerce.dto.CreateOrderRequest;
import com.loc.ecommerce.dto.OrderItemRequest;
import com.loc.ecommerce.dto.OrderResponse;
import com.loc.ecommerce.entity.Order;
import com.loc.ecommerce.entity.OrderItem;
import com.loc.ecommerce.entity.OrderStatus;
import com.loc.ecommerce.entity.Product;
import com.loc.ecommerce.exception.BusinessException;
import com.loc.ecommerce.exception.ResourceNotFoundException;
import com.loc.ecommerce.repository.OrderRepository;
import com.loc.ecommerce.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<OrderResponse> findAll() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    public OrderResponse findById(Long id) {
        Order order = getOrderOrThrow(id);
        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse create(CreateOrderRequest request) {
        Order order = new Order(OrderStatus.CREATED);

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id: " + itemRequest.productId()
                    ));

            if (itemRequest.quantity() > product.getStockQuantity()) {
                throw new BusinessException("Insufficient stock for product id: " + product.getId());
            }

            product.decreaseStock(itemRequest.quantity());
            order.addItem(new OrderItem(product, itemRequest.quantity()));
        }

        Order savedOrder = orderRepository.save(order);
        return OrderResponse.from(savedOrder);
    }

    private Order getOrderOrThrow(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }
}
