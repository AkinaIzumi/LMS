package com.library.management.service.serviceImpl;

import com.library.management.entity.BookHistory;
import com.library.management.entity.BorrowTransaction;
import com.library.management.repository.BookHistoryRepository;
import com.library.management.service.BookHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookHistoryServiceImpl implements BookHistoryService {

    @Autowired
    private BookHistoryRepository bookHistoryRepository;

    @Override
    public void saveHistory(BorrowTransaction transaction, String remarks) {
        BookHistory history = new BookHistory(
            transaction.getBook(),
            transaction.getMember(),
            transaction,
            transaction.getBorrowDate(),
            transaction.getDueDate(),
            transaction.getReturnDate(),
            transaction.getStatus(),
            remarks
        );
        bookHistoryRepository.save(history);
    }

    @Override
    public List<BookHistory> getAllHistories() {
        return bookHistoryRepository.findAll();
    }

    @Override
    public List<BookHistory> getHistoriesByBookId(Integer bookId) {
    return bookHistoryRepository.findByBookId(bookId);
}
}