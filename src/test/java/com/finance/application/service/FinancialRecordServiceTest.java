package com.finance.application.service;

import com.finance.api.dto.financial.CreateFinancialRecordRequest;
import com.finance.api.dto.financial.FinancialRecordResponse;
import com.finance.domain.entity.FinancialRecord;
import com.finance.domain.entity.User;
import com.finance.domain.enums.TransactionType;
import com.finance.exception.ResourceNotFoundException;
import com.finance.exception.UnauthorizedException;
import com.finance.infrastructure.repository.FinancialRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinancialRecordServiceTest {

    @Mock
    private FinancialRecordRepository recordRepository;

    @InjectMocks
    private FinancialRecordService recordService;

    private User testUser;
    private FinancialRecord testRecord;
    private CreateFinancialRecordRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .name("John Doe")
                .email("john@example.com")
                .build();

        testRecord = FinancialRecord.builder()
                .id(1)
                .amount(new BigDecimal("1000.00"))
                .type(TransactionType.INCOME)
                .category("Salary")
                .transactionDate(LocalDate.now())
                .description("Monthly salary")
                .createdBy(testUser)
                .isDeleted(false)
                .build();

        createRequest = CreateFinancialRecordRequest.builder()
                .amount(new BigDecimal("1000.00"))
                .type(TransactionType.INCOME)
                .category("Salary")
                .transactionDate(LocalDate.now())
                .description("Monthly salary")
                .build();
    }

    @Test
    void testCreateRecordSuccess() {
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(testRecord);

        FinancialRecordResponse response = recordService.createRecord(createRequest, testUser);

        assertNotNull(response);
        assertEquals(new BigDecimal("1000.00"), response.getAmount());
        assertEquals(TransactionType.INCOME, response.getType());
        verify(recordRepository, times(1)).save(any(FinancialRecord.class));
    }

    @Test
    void testGetRecordByIdSuccess() {
        when(recordRepository.findById(1)).thenReturn(Optional.of(testRecord));

        FinancialRecordResponse response = recordService.getRecordById(1, 1);

        assertNotNull(response);
        assertEquals(1, response.getId());
    }

    @Test
    void testGetRecordByIdNotFound() {
        when(recordRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recordService.getRecordById(999, 1));
    }

    @Test
    void testGetRecordByIdUnauthorized() {
        User otherUser = User.builder().id(2).build();
        testRecord.setCreatedBy(otherUser);
        when(recordRepository.findById(1)).thenReturn(Optional.of(testRecord));

        assertThrows(UnauthorizedException.class, () -> recordService.getRecordById(1, 1));
    }

    @Test
    void testDeleteRecordSuccess() {
        when(recordRepository.findById(1)).thenReturn(Optional.of(testRecord));
        when(recordRepository.save(any(FinancialRecord.class))).thenReturn(testRecord);

        recordService.deleteRecord(1, 1);

        assertTrue(testRecord.getIsDeleted());
        verify(recordRepository, times(1)).save(any(FinancialRecord.class));
    }

    @Test
    void testDeleteRecordUnauthorized() {
        User otherUser = User.builder().id(2).build();
        testRecord.setCreatedBy(otherUser);
        when(recordRepository.findById(1)).thenReturn(Optional.of(testRecord));

        assertThrows(UnauthorizedException.class, () -> recordService.deleteRecord(1, 1));
    }

    @Test
    void testGetTotalIncomeSuccess() {
        when(recordRepository.sumByTypeAndUser(1, TransactionType.INCOME))
                .thenReturn(new BigDecimal("5000.00"));

        BigDecimal total = recordService.getTotalIncome(1);

        assertEquals(new BigDecimal("5000.00"), total);
    }

    @Test
    void testGetTotalIncomeZero() {
        when(recordRepository.sumByTypeAndUser(1, TransactionType.INCOME)).thenReturn(null);

        BigDecimal total = recordService.getTotalIncome(1);

        assertEquals(BigDecimal.ZERO, total);
    }
}
