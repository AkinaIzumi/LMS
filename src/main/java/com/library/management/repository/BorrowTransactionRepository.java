package com.library.management.repository;

import com.library.management.entity.BorrowTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, Integer> {

    List<BorrowTransaction> findByMemberId(Integer memberId);

    List<BorrowTransaction> findByBookId(Integer bookId);

    List<BorrowTransaction> findByStatus(String status);
}