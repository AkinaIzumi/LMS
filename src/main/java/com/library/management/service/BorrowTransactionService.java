package com.library.management.service;

import com.library.management.entity.BorrowTransaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BorrowTransactionService {
    
    List<BorrowTransaction> getAllTransactions();

    Optional<BorrowTransaction> getTransactionById(Integer id);

    BorrowTransaction createTransaction(BorrowTransaction transaction);

    BorrowTransaction updateTransaction(Integer id, BorrowTransaction transactionDetails);

    void deleteTransaction(Integer id);

    List<BorrowTransaction> getTransactionsByMember(Integer memberId);

    void returnBook(Integer transactionId);

    List<BorrowTransaction> findByDueDateBeforeAndStatus(LocalDate date, String status);
}