package com.library.management.service;

import com.library.management.entity.Hold;
import java.util.List;

public interface HoldService {
    List<Hold> getAllHolds();
    Hold getHoldById(Integer id);
    Hold createHold(Hold hold, Integer bookId, Integer memberId);
    void cancelHold(Integer id);
    void deleteHold(Integer id);
}