package com.library.management.controller;

import com.library.management.entity.BorrowTransaction;
import com.library.management.service.BorrowTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class BorrowTransactionController {

    @Autowired
    private BorrowTransactionService borrowTransactionService;

    @GetMapping
    public ResponseEntity<List<BorrowTransaction>> getAllTransactions() {
        List<BorrowTransaction> transactions = borrowTransactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BorrowTransaction> getTransactionById(@PathVariable Integer id) {
        return borrowTransactionService.getTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


    @PostMapping
    public ResponseEntity<BorrowTransaction> createTransaction(@RequestBody BorrowTransaction transaction) {
        BorrowTransaction createdTransaction = borrowTransactionService.createTransaction(transaction);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BorrowTransaction> updateTransaction(@PathVariable Integer id, @RequestBody BorrowTransaction transactionDetails) {
        try {
            BorrowTransaction updatedTransaction = borrowTransactionService.updateTransaction(id, transactionDetails);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
        try {
            borrowTransactionService.deleteTransaction(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/member/{memberId}")
    public ResponseEntity<List<BorrowTransaction>> getTransactionsByMember(@PathVariable Integer memberId) {
        List<BorrowTransaction> transactions = borrowTransactionService.getTransactionsByMember(memberId);
        return ResponseEntity.ok(transactions);
    }
    @PostMapping("/return/{id}")
    public ResponseEntity<Void> returnBook(@PathVariable Integer id) {
        borrowTransactionService.returnBook(id);
        return ResponseEntity.ok().build();
    }
}