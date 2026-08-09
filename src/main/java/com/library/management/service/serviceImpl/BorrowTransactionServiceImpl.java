package com.library.management.service.serviceImpl;

import com.library.management.entity.Book;
import com.library.management.entity.BookHistory;
import com.library.management.entity.BorrowTransaction;
import com.library.management.repository.BookHistoryRepository;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowTransactionRepository;
import com.library.management.entity.Hold;
import com.library.management.repository.HoldRepository;
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
    private BookHistoryRepository bookHistoryRepository;

    @Autowired
    private HoldRepository holdRepository;

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
            book.setStatus("Borrowed");
        }
        
        bookRepository.save(book);

        if (transaction.getTransactionCode() == null || transaction.getTransactionCode().isEmpty()) {
            transaction.setTransactionCode("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        transaction.setBook(book);
        transaction.setStatus("Borrowed");
        if (transaction.getBorrowDate() == null) {
            transaction.setBorrowDate(LocalDate.now());
        }

        BorrowTransaction savedTransaction = borrowTransactionRepository.save(transaction);

        Hold pendingHold = holdRepository.findByBookIdAndMemberIdAndStatus(
                book.getId(), 
                transaction.getMember().getId(), 
                "PENDING"
        );

        if (pendingHold != null) {
            pendingHold.setStatus("SUCCESS");
            holdRepository.save(pendingHold);
        }

        BookHistory history = new BookHistory();
        history.setBook(book);
        history.setMember(transaction.getMember());
        history.setBorrowTransaction(savedTransaction);
        history.setBorrowDate(savedTransaction.getBorrowDate());
        history.setDueDate(savedTransaction.getDueDate()); 
        
        history.setStatus("Borrowed"); 
        
        history.setRemarks("Book borrowed with code: " + savedTransaction.getTransactionCode());
        
        bookHistoryRepository.save(history);

        return savedTransaction;
    }

   @Override
    @Transactional 
    public void returnBook(Integer transactionId, String returnStatus) {
        BorrowTransaction tx = borrowTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        LocalDate today = LocalDate.now();
        tx.setReturnDate(today); 

        Book book = tx.getBook();
        double bookPrice = (book != null) ? book.getPrice() : 0.0;
        LocalDate dueDate = tx.getDueDate();

        if ("loss or damage".equalsIgnoreCase(returnStatus)) {
            tx.setStatus("lOSS or DAMAGE");

            if (today.isAfter(dueDate)) {
                long overdueDays = ChronoUnit.DAYS.between(dueDate, today);
                double totalFine = (overdueDays * 1000.0) + bookPrice;
                tx.setFine(totalFine);
            } else {
                tx.setFine(bookPrice); 
            }

        } 

        else {
            tx.setStatus("RETURNED");

            if (today.isAfter(dueDate)) {
                long daysLate = ChronoUnit.DAYS.between(dueDate, today);
                double fineAmount = daysLate * 1000.0;
                tx.setFine(fineAmount);
            } else {
                tx.setFine(0.0);
            }

            if (book != null) {
                book.setAvailableQuantity(book.getAvailableQuantity() + 1);
                book.setStatus("AVAILABLE");
                bookRepository.save(book);
            }

            Hold nextHold = holdRepository.findFirstByBookIdAndStatusOrderByCreatedAtAsc(book.getId(), "PENDING");
                if (nextHold != null) {
                    System.out.println("⚠️ Alert: This book is held by " + nextHold.getMember().getName());
                }
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

    @Override
    public List<BorrowTransaction> findByDueDateBeforeAndStatus(LocalDate date, String status) {
        return borrowTransactionRepository.findByDueDateBeforeAndStatus(date, status);
    }

    
}