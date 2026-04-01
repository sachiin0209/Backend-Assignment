package com.finance.api.controller;

import com.finance.api.dto.user.CreateUserRequest;
import com.finance.api.dto.user.UpdateUserRequest;
import com.finance.api.dto.user.UserResponse;
import com.finance.application.service.UserService;
import com.finance.domain.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "User management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    /**
     * Create a new user (ADMIN only)
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user", description = "Create a new user (ADMIN only)")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        UserResponse response = userService.createUser(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get user by id
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Get user details by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId) {
        log.info("Fetching user with id: {}", userId);
        UserResponse response = userService.getUserById(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all users (ADMIN only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all users", description = "Get all users (ADMIN only)")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Fetching all users");
        List<UserResponse> responses = userService.getAllUsers();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Get users by role (ADMIN only)
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users by role", description = "Get users by specific role (ADMIN only)")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable UserRole role) {
        log.info("Fetching users with role: {}", role);
        List<UserResponse> responses = userService.getUsersByRole(role);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    /**
     * Update user (ADMIN only)
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Update user details (ADMIN only)")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Integer userId,
            @Valid @RequestBody UpdateUserRequest request) {
        log.info("Updating user with id: {}", userId);
        UserResponse response = userService.updateUser(userId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Activate user (ADMIN only)
     */
    @PostMapping("/{userId}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate user", description = "Activate a user (ADMIN only)")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Integer userId) {
        log.info("Activating user with id: {}", userId);
        UserResponse response = userService.activateUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Deactivate user (ADMIN only)
     */
    @PostMapping("/{userId}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Deactivate user", description = "Deactivate a user (ADMIN only)")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Integer userId) {
        log.info("Deactivating user with id: {}", userId);
        UserResponse response = userService.deactivateUser(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Assign role to user (ADMIN only)
     */
    @PostMapping("/{userId}/assign-role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign role to user", description = "Assign a role to user (ADMIN only)")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable Integer userId,
            @PathVariable UserRole role) {
        log.info("Assigning role {} to user with id: {}", role, userId);
        UserResponse response = userService.assignRole(userId, role);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
