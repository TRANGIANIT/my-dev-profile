package com.loc.ecommerce.service;

import com.loc.ecommerce.dto.ProductRequest;
import com.loc.ecommerce.dto.ProductResponse;
import com.loc.ecommerce.entity.Product;
import com.loc.ecommerce.exception.ResourceNotFoundException;
import com.loc.ecommerce.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {
    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    public ProductService(ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductResponse::from)
                .toList();
    }

    public Page<ProductResponse> search(
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            Pageable pageable
    ) {
        return productRepository.findAll(buildSpecification(keyword, minPrice, maxPrice, inStock), pageable)
                .map(ProductResponse::from);
    }

    public ProductResponse findById(Long id) {
        Product product = getProductOrThrow(id);
        return ProductResponse.from(product);
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        Product savedProduct = productRepository.save(product);
        return ProductResponse.from(savedProduct);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = getProductOrThrow(id);
        product.update(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        return ProductResponse.from(product);
    }

    @Transactional
    public void delete(Long id) {
        Product product = getProductOrThrow(id);
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponse uploadImage(Long id, MultipartFile file) {
        Product product = getProductOrThrow(id);
        String imageUrl = fileStorageService.storeProductImage(id, file);
        product.updateImageUrl(imageUrl);
        return ProductResponse.from(product);
    }

    private Product getProductOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Specification<Product> buildSpecification(
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock
    ) {
        Specification<Product> specification = Specification.where(null);

        if (keyword != null && !keyword.isBlank()) {
            String pattern = "%" + keyword.trim().toLowerCase() + "%";
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), pattern),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), pattern)
            ));
        }

        if (minPrice != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
        }

        if (maxPrice != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
        }

        if (Boolean.TRUE.equals(inStock)) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThan(root.get("stockQuantity"), 0));
        }

        return specification;
    }
}
