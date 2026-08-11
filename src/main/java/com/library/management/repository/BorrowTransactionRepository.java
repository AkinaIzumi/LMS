package com.library.management.repository;

import com.library.management.entity.BorrowTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, Integer> {

    List<BorrowTransaction> findByMemberId(Integer memberId);

    List<BorrowTransaction> findByBookId(Integer bookId);

    List<BorrowTransaction> findByStatus(String status);

    List<BorrowTransaction> findByDueDateBeforeAndStatus(LocalDate date, String status);

    List<BorrowTransaction> findByDueDateLessThanEqualAndStatus(LocalDate date, String status);

    long countByStatus(String status);

    List<BorrowTransaction> findTop5ByOrderByIdDesc();

    long countByBookIdAndStatusIn(Integer bookId, List<String> statuses);

    List<BorrowTransaction> findByBorrowDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT SUM(t.fine) FROM BorrowTransaction t WHERE t.borrowDate BETWEEN :startDate AND :endDate")
    Double getTotalFineBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}