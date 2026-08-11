package com.library.management.service;

import com.library.management.entity.BorrowTransaction;
import com.library.management.repository.BorrowTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReportService {

    @Autowired
    private BorrowTransactionRepository transactionRepository;

    public List<BorrowTransaction> getMonthlyTransactions(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth()); // လကုန်ရက်ကို ယူရန်
        
        return transactionRepository.findByBorrowDateBetween(startDate, endDate);
    }

    public Double getMonthlyTotalFine(int year, int month) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        
        Double total = transactionRepository.getTotalFineBetween(startDate, endDate);
        return total != null ? total : 0.0; // Null ဖြစ်နေပါက 0.0 ပြန်ရန်
    }
}