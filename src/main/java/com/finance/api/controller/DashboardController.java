package com.finance.api.controller;

import com.finance.api.dto.dashboard.DashboardSummaryResponse;
import com.finance.api.dto.dashboard.MonthlyTrendResponse;
import com.finance.api.dto.financial.FinancialRecordResponse;
import com.finance.application.service.DashboardService;
import com.finance.application.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard", description = "Dashboard and analytics endpoints")
@SecurityRequirement(name = "Bearer Authentication")
@PreAuthorize("hasAnyRole('VIEWER', 'ANALYST', 'ADMIN')")
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    /**
     * Get complete dashboard summary
     */
    @GetMapping("/summary")
    @Operation(summary = "Get dashboard summary", description = "Get complete dashboard summary with all analytics")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(Authentication authentication) {
        log.info("Fetching dashboard summary");

        Integer userId = extractUserIdFromAuthentication(authentication);
        DashboardSummaryResponse response = dashboardService.getDashboardSummary(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Get total income
     */
    @GetMapping("/income")
    @Operation(summary = "Get total income", description = "Get total income amount")
    public ResponseEntity<BigDecimal> getTotalIncome(Authentication authentication) {
        log.info("Fetching total income");

        Integer userId = extractUserIdFromAuthentication(authentication);
        BigDecimal income = dashboardService.getTotalIncome(userId);
        return new ResponseEntity<>(income, HttpStatus.OK);
    }

    /**
     * Get total expenses
     */
    @GetMapping("/expenses")
    @Operation(summary = "Get total expenses", description = "Get total expenses amount")
    public ResponseEntity<BigDecimal> getTotalExpenses(Authentication authentication) {
        log.info("Fetching total expenses");

        Integer userId = extractUserIdFromAuthentication(authentication);
        BigDecimal expenses = dashboardService.getTotalExpenses(userId);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    /**
     * Get net balance
     */
    @GetMapping("/balance")
    @Operation(summary = "Get net balance", description = "Get net balance (income - expenses)")
    public ResponseEntity<BigDecimal> getNetBalance(Authentication authentication) {
        log.info("Fetching net balance");

        Integer userId = extractUserIdFromAuthentication(authentication);
        BigDecimal balance = dashboardService.getNetBalance(userId);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }

    /**
     * Get category-wise totals
     */
    @GetMapping("/category-totals")
    @Operation(summary = "Get category-wise totals", description = "Get totals grouped by category")
    public ResponseEntity<Map<String, BigDecimal>> getCategoryWiseTotals(Authentication authentication) {
        log.info("Fetching category-wise totals");

        Integer userId = extractUserIdFromAuthentication(authentication);
        Map<String, BigDecimal> totals = dashboardService.getCategoryWiseTotals(userId);
        return new ResponseEntity<>(totals, HttpStatus.OK);
    }

    /**
     * Get monthly trends
     */
    @GetMapping("/monthly-trends")
    @Operation(summary = "Get monthly trends", description = "Get income and expenses trends by month")
    public ResponseEntity<List<MonthlyTrendResponse>> getMonthlyTrends(Authentication authentication) {
        log.info("Fetching monthly trends");

        Integer userId = extractUserIdFromAuthentication(authentication);
        List<MonthlyTrendResponse> trends = dashboardService.getMonthlyTrends(userId);
        return new ResponseEntity<>(trends, HttpStatus.OK);
    }

    /**
     * Get recent transactions
     */
    @GetMapping("/recent-transactions")
    @Operation(summary = "Get recent transactions", description = "Get most recent transactions")
    public ResponseEntity<List<FinancialRecordResponse>> getRecentTransactions(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        log.info("Fetching recent {} transactions", limit);

        Integer userId = extractUserIdFromAuthentication(authentication);
        List<FinancialRecordResponse> transactions = dashboardService.getRecentTransactions(userId, limit);
        return new ResponseEntity<>(transactions, HttpStatus.OK);
    }

    /**
     * Extract user id from authentication
     */
    private Integer extractUserIdFromAuthentication(Authentication authentication) {
        String email = authentication.getPrincipal().toString();
        return userService.getUserByEmail(email).getId();
    }
}
