package com.loc.ecommerce.dto;

import com.loc.ecommerce.entity.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(max = 80, message = "Username must be at most 80 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        String password,

        UserRole role
) {
    public UserRole resolvedRole() {
        return role == null ? UserRole.USER : role;
    }
}
