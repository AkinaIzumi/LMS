package com.library.management.controller;

import com.library.management.entity.BookHistory;
import com.library.management.service.BookHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/book-histories")
@RequiredArgsConstructor
public class BookHistoryController {

    private final BookHistoryService bookHistoryService;

    @GetMapping
    public List<BookHistory> getAllBookHistories() {
        return bookHistoryService.getAllHistories();
    }
}