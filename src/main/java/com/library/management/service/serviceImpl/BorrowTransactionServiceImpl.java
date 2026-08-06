package com.library.management.service.serviceImpl;

import com.library.management.entity.Book;
import com.library.management.entity.BorrowTransaction;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowTransactionRepository;
import com.library.management.service.BookService;
import com.library.management.service.BorrowTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BorrowTransactionServiceImpl implements BorrowTransactionService {

    @Autowired
    private BorrowTransactionRepository borrowTransactionRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookService bookService;
    @Override
    public List<BorrowTransaction> getAllTransactions() {
        return borrowTransactionRepository.findAll();
    }

    @Override
    public Optional<BorrowTransaction> getTransactionById(Integer id) {
        return borrowTransactionRepository.findById(id);
    }

    @Override
    @Transactional
    public BorrowTransaction updateTransaction(Integer id, BorrowTransaction transactionDetails) {
        BorrowTransaction existing = borrowTransactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if (transactionDetails.getMember() != null) {
            existing.setMember(transactionDetails.getMember());
        }

        if (transactionDetails.getDueDate() != null) {
            existing.setDueDate(transactionDetails.getDueDate());
        }

        if (transactionDetails.getStatus() != null) {
            existing.setStatus(transactionDetails.getStatus());
        }

        return borrowTransactionRepository.save(existing);
    }

    @Override
    @Transactional
    public BorrowTransaction createTransaction(BorrowTransaction transaction) {

        Book book = bookRepository.findById(transaction.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Book not found"));

        if (book.getAvailableQuantity() <= 0) {
            throw new RuntimeException("This book is currently out of stock!");
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);

        if (book.getAvailableQuantity() == 0) {
            book.setStatus("BORROWED");
        }
        
        bookRepository.save(book);

        if (transaction.getTransactionCode() == null || transaction.getTransactionCode().isEmpty()) {
            transaction.setTransactionCode("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        transaction.setBook(book);
        transaction.setStatus("BORROWED");
        if (transaction.getBorrowDate() == null) {
            transaction.setBorrowDate(LocalDate.now());
        }
        return borrowTransactionRepository.save(transaction);
    }

   @Override
    public void returnBook(Integer transactionId) {
        BorrowTransaction tx = borrowTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        LocalDate today = LocalDate.now();

        if (today.isAfter(tx.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(tx.getDueDate(), today);
            double fineAmount = daysLate * 1000.0;
            tx.setFine(fineAmount);
            tx.setStatus("OVERDUE");
        } else {
            tx.setFine(0.0);
            tx.setStatus("RETURNED");
        }

        Book book = tx.getBook();
        if (book != null) {
            book.setStatus("AVAILABLE");
            bookRepository.save(book);
        }

        borrowTransactionRepository.save(tx);
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