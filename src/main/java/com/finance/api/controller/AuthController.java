package com.finance.api.controller;

import com.finance.api.dto.auth.LoginRequest;
import com.finance.api.dto.auth.LoginResponse;
import com.finance.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication endpoints")
public class AuthController {

    private final AuthService authService;

    /**
     * Login endpoint
     */
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Login with email and password to get JWT token")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login request for user: {}", loginRequest.getEmail());
        LoginResponse response = authService.login(loginRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
