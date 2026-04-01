package com.finance.application.service;

import com.finance.api.dto.auth.LoginRequest;
import com.finance.api.dto.auth.LoginResponse;
import com.finance.domain.entity.User;
import com.finance.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Authenticate user and generate JWT token
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());

        try {
            User user = userService.getUserByEmail(request.getEmail());

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new UnauthorizedException("Invalid email or password");
            }

            String token = jwtService.generateToken(user);
            log.info("User authenticated successfully: {}", request.getEmail());

            return new LoginResponse(token, user.getId(), user.getEmail(), user.getRole().name());
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getEmail(), e);
            throw new UnauthorizedException("Invalid email or password");
        }
    }
}
