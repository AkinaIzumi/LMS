package com.library.management.service.serviceImpl;

import com.library.management.entity.Book;
import com.library.management.entity.Hold;
import com.library.management.entity.Member;
import com.library.management.repository.BookRepository;
import com.library.management.repository.HoldRepository;
import com.library.management.repository.MemberRepository;
import com.library.management.service.HoldService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class HoldServiceImpl implements HoldService {

    @Autowired
    private HoldRepository holdRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public List<Hold> getAllHolds() {
        return holdRepository.findAll();
    }

    @Override
    public Hold getHoldById(Integer id) {
        return holdRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hold record not found with id: " + id));
    }

    @Override
    @Transactional
    public Hold createHold(Hold hold, Integer bookId, Integer memberId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        if (book.getAvailableQuantity() > 0) {
            throw new RuntimeException("This book is currently available in stock. You can borrow it directly!");
        }

        if ("loss or damage".equalsIgnoreCase(book.getStatus())) {
            throw new RuntimeException("This book is lost or damaged and cannot be held.");
        }

        hold.setBook(book);
        hold.setMember(member);
        hold.setHoldDate(LocalDate.now());

        int days = (hold.getDuration() != null && hold.getDuration() > 0) ? hold.getDuration() : 3;
        hold.setDuration(days);
        hold.setExpiryDate(LocalDate.now().plusDays(days));
        hold.setStatus("PENDING");

        return holdRepository.save(hold);
    }

    @Override
    @Transactional
    public void cancelHold(Integer id) {
        Hold hold = getHoldById(id);
        hold.setStatus("CANCELLED");
        holdRepository.save(hold);
    }

    @Override
    @Transactional
    public void deleteHold(Integer id) {
        holdRepository.deleteById(id);
    }
}