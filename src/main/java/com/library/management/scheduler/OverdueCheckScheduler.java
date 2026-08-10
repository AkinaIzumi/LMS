package com.library.management.scheduler;

import com.library.management.entity.BorrowTransaction;
import com.library.management.entity.Hold;
import com.library.management.repository.BorrowTransactionRepository;
import com.library.management.repository.HoldRepository;
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

    @Autowired
    private HoldRepository holdRepository;

    @Scheduled(cron = "0 30 8 * * ?")
    public void checkOverdueBooks() {
        LocalDate today = LocalDate.now();

        List<BorrowTransaction> overdueTransactions = transactionRepository.findByDueDateLessThanEqualAndStatus(today, "BORROWED");

        for (BorrowTransaction tx : overdueTransactions) {
            LocalDate dueDate = tx.getDueDate();

            long daysLate = ChronoUnit.DAYS.between(dueDate, today);
            
            double fineAmount = daysLate > 0 ? daysLate * 1000.0 : 0.0; 
            
            tx.setFine(fineAmount);
            
            if (dueDate.isBefore(today)) {
                tx.setStatus("OVERDUE");
            }
            
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

        List<Hold> expiredHolds = holdRepository.findByExpiryDateBeforeAndStatus(today, "PENDING");

        for (Hold hold : expiredHolds) {
            hold.setStatus("EXPIRED");
            holdRepository.save(hold);
            
            if (hold.getMember() != null && hold.getMember().getEmail() != null) {
                try {
                    emailService.sendEmail(
                            hold.getMember().getEmail(),
                            "Book Hold Expired Notice",
                            "Hello " + hold.getMember().getName() + ",\n\n"
                            + "Your hold for the book \"" + hold.getBook().getTitle() + "\" has expired. Therefore, the booking status has been updated to EXPIRED.\n\n"
                            + "Thank you,\nLibrary Management System"
                    );
                } catch (Exception e) {
                    System.err.println("Failed to send hold expiry email to " + hold.getMember().getEmail() + ": " + e.getMessage());
                }
            }
        }
    }
}