package com.loc.ecommerce.service;

import com.loc.ecommerce.dto.AuthResponse;
import com.loc.ecommerce.dto.LoginRequest;
import com.loc.ecommerce.dto.RegisterRequest;
import com.loc.ecommerce.entity.AppUser;
import com.loc.ecommerce.exception.BusinessException;
import com.loc.ecommerce.repository.UserRepository;
import com.loc.ecommerce.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException("Username is already registered");
        }

        AppUser user = new AppUser(
                request.username(),
                passwordEncoder.encode(request.password()),
                request.resolvedRole()
        );
        AppUser savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getUsername(), savedUser.getRole().name());
        return new AuthResponse(token, "Bearer", savedUser.getUsername(), savedUser.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        String role = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .findFirst()
                .orElse("USER");
        String token = jwtService.generateToken(authentication.getName(), role);
        return new AuthResponse(token, "Bearer", authentication.getName(), role);
    }
}
