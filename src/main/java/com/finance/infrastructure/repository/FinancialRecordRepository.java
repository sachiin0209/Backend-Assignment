package com.finance.infrastructure.repository;

import com.finance.domain.entity.FinancialRecord;
import com.finance.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Integer> {

    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.isDeleted = false AND fr.createdBy.id = :userId " +
            "ORDER BY fr.transactionDate DESC")
    Page<FinancialRecord> findByCreatedByIdNotDeleted(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.isDeleted = false AND fr.createdBy.id = :userId " +
            "AND fr.transactionDate >= :startDate AND fr.transactionDate <= :endDate " +
            "ORDER BY fr.transactionDate DESC")
    List<FinancialRecord> findByDateRange(@Param("userId") Integer userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.isDeleted = false AND fr.createdBy.id = :userId " +
            "AND fr.type = :type ORDER BY fr.transactionDate DESC")
    List<FinancialRecord> findByType(@Param("userId") Integer userId, @Param("type") TransactionType type);

    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.isDeleted = false AND fr.createdBy.id = :userId " +
            "AND fr.category = :category ORDER BY fr.transactionDate DESC")
    List<FinancialRecord> findByCategory(@Param("userId") Integer userId, @Param("category") String category);

    @Query("SELECT SUM(fr.amount) FROM FinancialRecord fr WHERE fr.isDeleted = false AND fr.createdBy.id = :userId " +
            "AND fr.type = :type")
    BigDecimal sumByTypeAndUser(@Param("userId") Integer userId, @Param("type") TransactionType type);

    @Query("SELECT COUNT(fr) FROM FinancialRecord fr WHERE fr.isDeleted = false AND fr.createdBy.id = :userId")
    long countActiveRecordsByUser(@Param("userId") Integer userId);

    @Query("SELECT fr FROM FinancialRecord fr WHERE fr.isDeleted = false AND fr.createdBy.id = :userId " +
            "ORDER BY fr.transactionDate DESC LIMIT :limit")
    List<FinancialRecord> findRecentRecords(@Param("userId") Integer userId, @Param("limit") int limit);

    @Query(value = "SELECT TO_CHAR(fr.transaction_date, 'YYYY-MM') as month, " +
            "SUM(CASE WHEN fr.type = 'INCOME' THEN fr.amount ELSE 0 END) as income, " +
            "SUM(CASE WHEN fr.type = 'EXPENSE' THEN fr.amount ELSE 0 END) as expenses " +
            "FROM financial_records fr WHERE fr.is_deleted = false AND fr.created_by = :userId " +
            "GROUP BY TO_CHAR(fr.transaction_date, 'YYYY-MM') " +
            "ORDER BY month DESC", nativeQuery = true)
    List<Object[]> findMonthlyTrends(@Param("userId") Integer userId);

    @Query("SELECT fr.category, SUM(fr.amount) FROM FinancialRecord fr WHERE fr.isDeleted = false " +
            "AND fr.createdBy.id = :userId GROUP BY fr.category")
    List<Object[]> findCategoryWiseTotals(@Param("userId") Integer userId);
}
