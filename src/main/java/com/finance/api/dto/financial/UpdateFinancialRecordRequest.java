package com.finance.api.dto.financial;

import com.finance.domain.enums.TransactionType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateFinancialRecordRequest {

    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;

    private TransactionType type;

    @Size(min = 1, max = 100, message = "Category must be between 1 and 100 characters")
    private String category;

    @PastOrPresent(message = "Transaction date cannot be in the future")
    private LocalDate transactionDate;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;
}
