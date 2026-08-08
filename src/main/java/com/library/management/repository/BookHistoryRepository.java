package com.library.management.repository;

import com.library.management.entity.BookHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookHistoryRepository extends JpaRepository<BookHistory, Integer> {
    
    List<BookHistory> findByBookId(Integer bookId);
    List<BookHistory> findByMemberId(Integer memberId);
}