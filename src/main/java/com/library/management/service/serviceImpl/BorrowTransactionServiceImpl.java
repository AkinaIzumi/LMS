package com.library.management.service.serviceImpl;

import com.library.management.entity.BorrowTransaction;
import com.library.management.repository.BorrowTransactionRepository;
import com.library.management.service.BorrowTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BorrowTransactionServiceImpl implements BorrowTransactionService {

    @Autowired
    private BorrowTransactionRepository borrowTransactionRepository;

    @Override
    public List<BorrowTransaction> getAllTransactions() {
        return borrowTransactionRepository.findAll();
    }

    @Override
    public Optional<BorrowTransaction> getTransactionById(Integer id) {
        return borrowTransactionRepository.findById(id);
    }

    @Override
    public BorrowTransaction createTransaction(BorrowTransaction transaction) {
        return borrowTransactionRepository.save(transaction);
    }

    @Override
    public BorrowTransaction updateTransaction(Integer id, BorrowTransaction transactionDetails) {
        BorrowTransaction transaction = borrowTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("BorrowTransaction not found with id: " + id));

        transaction.setMember(transactionDetails.getMember());
        transaction.setBook(transactionDetails.getBook());
        transaction.setBorrowDate(transactionDetails.getBorrowDate());
        transaction.setDueDate(transactionDetails.getDueDate());
        transaction.setStatus(transactionDetails.getStatus());

        return borrowTransactionRepository.save(transaction);
    }

    @Override
    public void deleteTransaction(Integer id) {
        borrowTransactionRepository.deleteById(id);
    }

    @Override
    public List<BorrowTransaction> getTransactionsByMember(Integer memberId) {
        return borrowTransactionRepository.findByMemberId(memberId);
    }
}