package com.loc.ecommerce.service;

import com.loc.ecommerce.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    private final Path uploadDir;

    public FileStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
    }

    public String storeProductImage(Long productId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("Image file is required");
        }
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new BusinessException("Only JPEG, PNG, and WebP images are allowed");
        }

        try {
            Path productDir = uploadDir.resolve("products");
            Files.createDirectories(productDir);

            String extension = extensionFrom(file.getContentType());
            String filename = productId + "-" + UUID.randomUUID() + extension;
            Path target = productDir.resolve(filename).normalize();
            file.transferTo(target);
            return "/uploads/products/" + filename;
        } catch (IOException exception) {
            throw new BusinessException("Failed to store product image");
        }
    }

    private String extensionFrom(String contentType) {
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }
}
