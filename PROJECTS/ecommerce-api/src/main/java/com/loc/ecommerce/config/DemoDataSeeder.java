package com.loc.ecommerce.config;

import com.loc.ecommerce.entity.AppUser;
import com.loc.ecommerce.entity.Product;
import com.loc.ecommerce.entity.UserRole;
import com.loc.ecommerce.repository.ProductRepository;
import com.loc.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DemoDataSeeder implements ApplicationRunner {
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final boolean seedDemoData;

    public DemoDataSeeder(
            ProductRepository productRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.seed-demo-data:false}") boolean seedDemoData
    ) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedDemoData = seedDemoData;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedDemoData) {
            return;
        }

        seedUsers();
        seedProducts();
    }

    private void seedUsers() {
        if (!userRepository.existsByUsername("admin@example.com")) {
            userRepository.save(new AppUser(
                    "admin@example.com",
                    passwordEncoder.encode("password123"),
                    UserRole.ADMIN
            ));
        }

        if (!userRepository.existsByUsername("user@example.com")) {
            userRepository.save(new AppUser(
                    "user@example.com",
                    passwordEncoder.encode("password123"),
                    UserRole.USER
            ));
        }
    }

    private void seedProducts() {
        if (productRepository.count() > 0) {
            return;
        }

        productRepository.saveAll(List.of(
                new Product("Mechanical Keyboard", "Hot-swappable keyboard for office and gaming", BigDecimal.valueOf(12800), 12),
                new Product("Wireless Mouse", "Lightweight mouse with silent clicks", BigDecimal.valueOf(5400), 25),
                new Product("USB-C Hub", "7-in-1 adapter for laptop workstations", BigDecimal.valueOf(7200), 18),
                new Product("27 Inch Monitor", "QHD monitor for development and design work", BigDecimal.valueOf(39800), 7),
                new Product("Laptop Stand", "Aluminum stand for ergonomic desk setup", BigDecimal.valueOf(4300), 15)
        ));
    }
}
