package com.finance.api.controller;

import com.finance.api.dto.financial.CreateFinancialRecordRequest;
import com.finance.api.dto.financial.FinancialRecordResponse;
import com.finance.api.dto.financial.UpdateFinancialRecordRequest;
import com.finance.application.service.FinancialRecordService;
import com.finance.application.service.UserService;
import com.finance.domain.entity.User;
import com.finance.domain.enums.TransactionType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Financial Records", description = "Financial record management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
public class FinancialRecordController {

    private final FinancialRecordService recordService;
    private final UserService userService;

    /**
     * Create a new financial record
     */
    @PostMapping
    @Operation(summary = "Create financial record", description = "Create a new financial record")
    public ResponseEntity<FinancialRecordResponse> createRecord(
            @Valid @RequestBody CreateFinancialRecordRequest request,
            Authentication authentication) {
        log.info("Creating financial record");

        User creator = extractUserFromAuthentication(authentication);
        FinancialRecordResponse response = recordService.createRecord(request, creator);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get record by id
     */
    @GetMapping("/{recordId}")
    @Operation(summary = "Get financial record", description = "Get financial record by ID")
    public ResponseEntity<FinancialRecordResponse> getRecordById(
            @PathVariable Integer recordId,
            Authentication authentication) {
        log.info("Fetching record with id: {}", recordId);

        Integer userId = extractUserIdFromAuthentication(authentication);
        FinancialRecordResponse response = recordService.getRecordById(recordId, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get all records with pagination
     */
    @GetMapping
    @Operation(summary = "Get all records", description = "Get all financial records with pagination")
    public ResponseEntity<Page<FinancialRecordResponse>> getAllRecords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        log.info("Fetching records with pagination: page={}, size={}", page, size);

        Integer userId = extractUserIdFromAuthentication(authentication);
        Page<FinancialRecordResponse> response = recordService.getUserRecords(userId, page, size);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get records by date range
     */
    @GetMapping("/filter/date-range")
    @Operation(summary = "Get records by date range", description = "Get financial records within a date range")
    public ResponseEntity<List<FinancialRecordResponse>> getRecordsByDateRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            Authentication authentication) {
        log.info("Fetching records between {} and {}", startDate, endDate);

        Integer userId = extractUserIdFromAuthentication(authentication);
        List<FinancialRecordResponse> response = recordService.getRecordsByDateRange(userId, startDate, endDate);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get records by type
     */
    @GetMapping("/filter/type/{type}")
    @Operation(summary = "Get records by type", description = "Get financial records by transaction type")
    public ResponseEntity<List<FinancialRecordResponse>> getRecordsByType(
            @PathVariable TransactionType type,
            Authentication authentication) {
        log.info("Fetching records with type: {}", type);

        Integer userId = extractUserIdFromAuthentication(authentication);
        List<FinancialRecordResponse> response = recordService.getRecordsByType(userId, type);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get records by category
     */
    @GetMapping("/filter/category/{category}")
    @Operation(summary = "Get records by category", description = "Get financial records by category")
    public ResponseEntity<List<FinancialRecordResponse>> getRecordsByCategory(
            @PathVariable String category,
            Authentication authentication) {
        log.info("Fetching records with category: {}", category);

        Integer userId = extractUserIdFromAuthentication(authentication);
        List<FinancialRecordResponse> response = recordService.getRecordsByCategory(userId, category);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Update a record
     */
    @PutMapping("/{recordId}")
    @Operation(summary = "Update financial record", description = "Update an existing financial record")
    public ResponseEntity<FinancialRecordResponse> updateRecord(
            @PathVariable Integer recordId,
            @Valid @RequestBody UpdateFinancialRecordRequest request,
            Authentication authentication) {
        log.info("Updating record with id: {}", recordId);

        Integer userId = extractUserIdFromAuthentication(authentication);
        FinancialRecordResponse response = recordService.updateRecord(recordId, request, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Delete a record (soft delete)
     */
    @DeleteMapping("/{recordId}")
    @Operation(summary = "Delete financial record", description = "Delete a financial record (soft delete)")
    public ResponseEntity<Void> deleteRecord(
            @PathVariable Integer recordId,
            Authentication authentication) {
        log.info("Deleting record with id: {}", recordId);

        Integer userId = extractUserIdFromAuthentication(authentication);
        recordService.deleteRecord(recordId, userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Extract user id from authentication
     */
    private Integer extractUserIdFromAuthentication(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return userService.getUserByEmail(email).getId();
    }

    /**
     * Extract user from authentication
     */
    private User extractUserFromAuthentication(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return userService.getUserByEmail(email);
    }
}
