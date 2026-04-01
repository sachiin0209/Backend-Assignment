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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private CreateUserRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .role(UserRole.ANALYST)
                .status(UserStatus.ACTIVE)
                .build();

        createRequest = CreateUserRequest.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .password("password123")
                .role(UserRole.VIEWER)
                .build();
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.createUser(createRequest);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals(UserRole.ANALYST, response.getRole());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserWithExistingEmail() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(InvalidRequestException.class, () -> userService.createUser(createRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByIdSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));

        UserResponse response = userService.getUserById(1);

        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("John Doe", response.getName());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999));
    }

    @Test
    void testGetUserByEmailSuccess() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        User user = userService.getUserByEmail("john@example.com");

        assertNotNull(user);
        assertEquals("john@example.com", user.getEmail());
    }

    @Test
    void testActivateUser() {
        testUser.setStatus(UserStatus.INACTIVE);
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.activateUser(1);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeactivateUser() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.deactivateUser(1);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testAssignRole() {
        when(userRepository.findById(1)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserResponse response = userService.assignRole(1, UserRole.ADMIN);

        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }
}
