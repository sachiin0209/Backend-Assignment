package com.finance.api.dto.dashboard;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyTrendResponse {
    private String month;  // Format: YYYY-MM
    private BigDecimal income;
    private BigDecimal expenses;
    private BigDecimal netAmount;
}
