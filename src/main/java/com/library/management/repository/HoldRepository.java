package com.library.management.repository;

import com.library.management.entity.Hold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HoldRepository extends JpaRepository<Hold, Integer> {
    List<Hold> findByBookId(Integer bookId);
    List<Hold> findByMemberId(Integer memberId);
    Hold findByBookIdAndMemberIdAndStatus(Integer bookId, Integer memberId, String status);

    Hold findFirstByBookIdAndStatusOrderByCreatedAtAsc(Integer bookId, String status);
}