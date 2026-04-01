package com.finance.application.service;

import com.finance.api.dto.user.CreateUserRequest;
import com.finance.api.dto.user.UpdateUserRequest;
import com.finance.api.dto.user.UserResponse;
import com.finance.domain.entity.User;
import com.finance.domain.enums.UserRole;
import com.finance.domain.enums.UserStatus;
import com.finance.exception.InvalidRequestException;
import com.finance.exception.ResourceNotFoundException;
import com.finance.infrastructure.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create a new user
     */
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new InvalidRequestException("User with email already exists: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return mapToResponse(savedUser);
    }

    /**
     * Get user by id
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Integer userId) {
        log.debug("Fetching user with id: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return mapToResponse(user);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(UserRole role) {
        log.debug("Fetching users with role: {}", role);
        return userRepository.findByRole(role).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update user
     */
    public UserResponse updateUser(Integer userId, UpdateUserRequest request) {
        log.info("Updating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            if (!user.getEmail().equals(request.getEmail()) && userRepository.existsByEmail(request.getEmail())) {
                throw new InvalidRequestException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", userId);

        return mapToResponse(updatedUser);
    }

    /**
     * Activate user
     */
    public UserResponse activateUser(Integer userId) {
        log.info("Activating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        log.info("User activated successfully with id: {}", userId);

        return mapToResponse(savedUser);
    }

    /**
     * Deactivate user
     */
    public UserResponse deactivateUser(Integer userId) {
        log.info("Deactivating user with id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setStatus(UserStatus.INACTIVE);
        User savedUser = userRepository.save(user);
        log.info("User deactivated successfully with id: {}", userId);

        return mapToResponse(savedUser);
    }

    /**
     * Assign role to user
     */
    public UserResponse assignRole(Integer userId, UserRole role) {
        log.info("Assigning role {} to user with id: {}", role, userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setRole(role);
        User savedUser = userRepository.save(user);
        log.info("Role assigned successfully to user with id: {}", userId);

        return mapToResponse(savedUser);
    }

    /**
     * Map User entity to UserResponse DTO
     */
    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
