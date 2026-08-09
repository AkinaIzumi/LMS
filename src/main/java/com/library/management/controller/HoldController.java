package com.library.management.controller;

import com.library.management.entity.Hold;
import com.library.management.service.BookService;
import com.library.management.service.HoldService;
import com.library.management.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequestMapping("/holds")
@RequiredArgsConstructor
public class HoldController {

    private final HoldService holdService;
    private final BookService bookService;
    private final MemberService memberService;

    @GetMapping
    public String viewHoldsList(Model model) {
        model.addAttribute("holds", holdService.getAllHolds());
        return "holds/list";
    }

    @GetMapping("/new")
    public String showCreateHoldForm(Model model) {
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("members", memberService.getAllMembers());
        return "holds/create";
    }

    @PostMapping("/api/save")
    @ResponseBody
    public ResponseEntity<?> createHoldFromJson(@RequestBody Map<String, Object> payload) {
        try {
            Integer bookId = Integer.valueOf(payload.get("bookId").toString());
            Integer memberId = Integer.valueOf(payload.get("memberId").toString());
            Integer duration = Integer.valueOf(payload.get("duration").toString());

            Hold hold = new Hold();
            hold.setDuration(duration);

            holdService.createHold(hold, bookId, memberId);
            return ResponseEntity.ok(Map.of("success", true, "message", "Book hold created successfully via JSON!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @GetMapping("/cancel/{id}")
    public String cancelHold(@PathVariable("id") Integer id) {
        holdService.cancelHold(id);
        return "redirect:/holds";
    }

    @GetMapping("/delete/{id}")
    public String deleteHold(@PathVariable("id") Integer id) {
        holdService.deleteHold(id);
        return "redirect:/holds";
    }
}