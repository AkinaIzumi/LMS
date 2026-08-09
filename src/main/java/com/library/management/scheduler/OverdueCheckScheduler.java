package com.library.management.scheduler;

import com.library.management.entity.BorrowTransaction;
import com.library.management.repository.BorrowTransactionRepository;
import com.library.management.service.BorrowTransactionService;
import com.library.management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class OverdueCheckScheduler {

    @Autowired
    private BorrowTransactionRepository transactionRepository;

    @Autowired
    private BorrowTransactionService borrowTransactionService;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkOverdueBooks() {
        LocalDate today = LocalDate.now();
        
        List<BorrowTransaction> overdueTransactions = transactionRepository.findByDueDateBeforeAndStatus(today, "BORROWED");

        for (BorrowTransaction tx : overdueTransactions) {
            LocalDate dueDate = tx.getDueDate();

            long daysLate = ChronoUnit.DAYS.between(dueDate, today);
            double fineAmount = daysLate * 1000.0; 
            
            tx.setFine(fineAmount);
            tx.setStatus("OVERDUE");
            
            borrowTransactionService.updateTransaction(tx.getId(), tx);

            if (tx.getMember() != null && tx.getMember().getEmail() != null) {
                try {
                    emailService.sendOverdueEmail(
                            tx.getMember().getEmail(),
                            tx.getMember().getName(),
                            tx.getBook().getTitle(),
                            tx.getDueDate().toString(),
                            tx.getFine()
                    );
                } catch (Exception e) {
                    System.err.println("Failed to send email to " + tx.getMember().getEmail() + ": " + e.getMessage());
                }
            }
        }
    }
}