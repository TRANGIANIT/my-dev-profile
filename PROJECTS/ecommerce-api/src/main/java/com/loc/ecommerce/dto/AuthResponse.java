package com.loc.ecommerce.dto;

public record AuthResponse(
        String token,
        String refreshToken,
        String tokenType,
        long expiresIn,
        String username,
        String role
) {
}
