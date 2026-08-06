package com.library.management.scheduler;

import com.library.management.entity.BorrowTransaction;
import com.library.management.repository.BorrowTransactionRepository;
import com.library.management.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class OverdueCheckScheduler {

    @Autowired
    private BorrowTransactionRepository transactionRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * ?")
    public void checkOverdueBooks() {
        LocalDate today = LocalDate.now();
        
        List<BorrowTransaction> overdueTransactions = transactionRepository.findByDueDateBeforeAndStatus(today, "BORROWED");

        for (BorrowTransaction tx : overdueTransactions) {
            tx.setStatus("OVERDUE");
            transactionRepository.save(tx);

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