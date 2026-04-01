package com.finance.api.dto.financial;

import com.finance.domain.enums.TransactionType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialRecordResponse {
    private Integer id;
    private BigDecimal amount;
    private TransactionType type;
    private String category;
    private LocalDate transactionDate;
    private String description;
    private Integer createdBy;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
