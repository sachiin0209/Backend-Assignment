package com.finance.application.service;

import com.finance.api.dto.financial.CreateFinancialRecordRequest;
import com.finance.api.dto.financial.FinancialRecordResponse;
import com.finance.api.dto.financial.UpdateFinancialRecordRequest;
import com.finance.domain.entity.FinancialRecord;
import com.finance.domain.entity.User;
import com.finance.domain.enums.TransactionType;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.infrastructure.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;

    /**
     * Create a new financial record
     */
    public FinancialRecordResponse createRecord(CreateFinancialRecordRequest request, User creator) {
        log.info("Creating financial record for user: {}", creator.getEmail());

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .transactionDate(request.getTransactionDate())
                .description(request.getDescription())
                .createdBy(creator)
                .isDeleted(false)
                .build();

        FinancialRecord savedRecord = recordRepository.save(record);
        log.info("Financial record created successfully with id: {}", savedRecord.getId());

        return mapToResponse(savedRecord);
    }

    /**
     * Get record by id
     */
    @Transactional(readOnly = true)
    public FinancialRecordResponse getRecordById(Integer recordId, Integer userId) {
        log.debug("Fetching record with id: {}", recordId);

        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + recordId));

        // Verify ownership
        if (!record.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to access this record");
        }

        if (record.getIsDeleted()) {
            throw new ResourceNotFoundException("Record not found with id: " + recordId);
        }

        return mapToResponse(record);
    }

    /**
     * Get all records for a user with pagination
     */
    @Transactional(readOnly = true)
    public Page<FinancialRecordResponse> getUserRecords(Integer userId, int page, int size) {
        log.debug("Fetching records for user: {} with pagination page: {}, size: {}", userId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<FinancialRecord> records = recordRepository.findByCreatedByIdNotDeleted(userId, pageable);

        return records.map(this::mapToResponse);
    }

    /**
     * Get records by date range
     */
    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> getRecordsByDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        log.debug("Fetching records for user: {} between {} and {}", userId, startDate, endDate);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        List<FinancialRecord> records = recordRepository.findByDateRange(userId, startDate, endDate);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get records by type
     */
    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> getRecordsByType(Integer userId, TransactionType type) {
        log.debug("Fetching records for user: {} with type: {}", userId, type);

        List<FinancialRecord> records = recordRepository.findByType(userId, type);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get records by category
     */
    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> getRecordsByCategory(Integer userId, String category) {
        log.debug("Fetching records for user: {} with category: {}", userId, category);

        List<FinancialRecord> records = recordRepository.findByCategory(userId, category);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update a record
     */
    public FinancialRecordResponse updateRecord(Integer recordId, UpdateFinancialRecordRequest request, Integer userId) {
        log.info("Updating record with id: {}", recordId);

        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + recordId));

        // Verify ownership
        if (!record.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to update this record");
        }

        if (record.getIsDeleted()) {
            throw new ResourceNotFoundException("Record not found with id: " + recordId);
        }

        if (request.getAmount() != null) {
            record.setAmount(request.getAmount());
        }
        if (request.getType() != null) {
            record.setType(request.getType());
        }
        if (request.getCategory() != null) {
            record.setCategory(request.getCategory());
        }
        if (request.getTransactionDate() != null) {
            record.setTransactionDate(request.getTransactionDate());
        }
        if (request.getDescription() != null) {
            record.setDescription(request.getDescription());
        }

        FinancialRecord updatedRecord = recordRepository.save(record);
        log.info("Record updated successfully with id: {}", recordId);

        return mapToResponse(updatedRecord);
    }

    /**
     * Delete a record (soft delete)
     */
    public void deleteRecord(Integer recordId, Integer userId) {
        log.info("Deleting record with id: {}", recordId);

        FinancialRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Record not found with id: " + recordId));

        // Verify ownership
        if (!record.getCreatedBy().getId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to delete this record");
        }

        record.setIsDeleted(true);
        recordRepository.save(record);
        log.info("Record deleted successfully with id: {}", recordId);
    }

    /**
     * Get total income for a user
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalIncome(Integer userId) {
        BigDecimal total = recordRepository.sumByTypeAndUser(userId, TransactionType.INCOME);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get total expenses for a user
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalExpenses(Integer userId) {
        BigDecimal total = recordRepository.sumByTypeAndUser(userId, TransactionType.EXPENSE);
        return total != null ? total : BigDecimal.ZERO;
    }

    /**
     * Get recent records
     */
    @Transactional(readOnly = true)
    public List<FinancialRecordResponse> getRecentRecords(Integer userId, int limit) {
        log.debug("Fetching recent {} records for user: {}", limit, userId);

        List<FinancialRecord> records = recordRepository.findRecentRecords(userId, limit);
        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Map FinancialRecord entity to FinancialRecordResponse DTO
     */
    private FinancialRecordResponse mapToResponse(FinancialRecord record) {
        return FinancialRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType())
                .category(record.getCategory())
                .transactionDate(record.getTransactionDate())
                .description(record.getDescription())
                .createdBy(record.getCreatedBy().getId())
                .createdByName(record.getCreatedBy().getName())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
