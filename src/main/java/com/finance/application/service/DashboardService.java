package com.finance.application.service;

import com.finance.api.dto.dashboard.DashboardSummaryResponse;
import com.finance.api.dto.dashboard.MonthlyTrendResponse;
import com.finance.api.dto.financial.FinancialRecordResponse;
import com.finance.infrastructure.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {

    private final FinancialRecordRepository recordRepository;
    private final FinancialRecordService financialRecordService;

    /**
     * Get complete dashboard summary
     */
    public DashboardSummaryResponse getDashboardSummary(Integer userId) {
        log.debug("Generating dashboard summary for user: {}", userId);

        BigDecimal totalIncome = financialRecordService.getTotalIncome(userId);
        BigDecimal totalExpenses = financialRecordService.getTotalExpenses(userId);
        BigDecimal netBalance = totalIncome.subtract(totalExpenses);

        Map<String, BigDecimal> categoryWiseTotals = getCategoryWiseTotals(userId);
        List<MonthlyTrendResponse> monthlyTrends = getMonthlyTrends(userId);
        long totalTransactions = recordRepository.countActiveRecordsByUser(userId);

        return DashboardSummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .netBalance(netBalance)
                .categoryWiseTotals(categoryWiseTotals)
                .monthlyTrends(monthlyTrends)
                .totalTransactions((int) totalTransactions)
                .build();
    }

    /**
     * Get total income
     */
    public BigDecimal getTotalIncome(Integer userId) {
        log.debug("Calculating total income for user: {}", userId);
        return financialRecordService.getTotalIncome(userId);
    }

    /**
     * Get total expenses
     */
    public BigDecimal getTotalExpenses(Integer userId) {
        log.debug("Calculating total expenses for user: {}", userId);
        return financialRecordService.getTotalExpenses(userId);
    }

    /**
     * Get net balance (income - expenses)
     */
    public BigDecimal getNetBalance(Integer userId) {
        log.debug("Calculating net balance for user: {}", userId);
        BigDecimal income = getTotalIncome(userId);
        BigDecimal expenses = getTotalExpenses(userId);
        return income.subtract(expenses);
    }

    /**
     * Get category-wise totals
     */
    public Map<String, BigDecimal> getCategoryWiseTotals(Integer userId) {
        log.debug("Calculating category-wise totals for user: {}", userId);

        List<Object[]> results = recordRepository.findCategoryWiseTotals(userId);
        Map<String, BigDecimal> categoryTotals = new LinkedHashMap<>();

        for (Object[] result : results) {
            String category = (String) result[0];
            BigDecimal total = (BigDecimal) result[1];
            categoryTotals.put(category, total);
        }

        return categoryTotals;
    }

    /**
     * Get monthly trends
     */
    public List<MonthlyTrendResponse> getMonthlyTrends(Integer userId) {
        log.debug("Calculating monthly trends for user: {}", userId);

        List<Object[]> results = recordRepository.findMonthlyTrends(userId);
        List<MonthlyTrendResponse> trends = new ArrayList<>();

        for (Object[] result : results) {
            String month = (String) result[0];
            BigDecimal income = result[1] != null ? (BigDecimal) result[1] : BigDecimal.ZERO;
            BigDecimal expenses = result[2] != null ? (BigDecimal) result[2] : BigDecimal.ZERO;
            BigDecimal netAmount = income.subtract(expenses);

            trends.add(MonthlyTrendResponse.builder()
                    .month(month)
                    .income(income)
                    .expenses(expenses)
                    .netAmount(netAmount)
                    .build());
        }

        return trends;
    }

    /**
     * Get recent transactions
     */
    public List<FinancialRecordResponse> getRecentTransactions(Integer userId, int limit) {
        log.debug("Fetching recent {} transactions for user: {}", limit, userId);
        return financialRecordService.getRecentRecords(userId, Math.min(limit, 100));
    }
}
