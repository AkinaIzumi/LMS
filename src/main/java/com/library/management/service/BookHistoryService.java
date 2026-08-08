package com.library.management.service;

import com.library.management.entity.BookHistory;
import com.library.management.entity.BorrowTransaction;

import java.util.List;

public interface BookHistoryService {

    void saveHistory(BorrowTransaction transaction, String remarks);

    List<BookHistory> getAllHistories();

    List<BookHistory> getHistoriesByBookId(Integer bookId);
}