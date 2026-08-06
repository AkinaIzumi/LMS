package com.library.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.management.service.BookService;
import com.library.management.service.CategoryService;
import com.library.management.entity.BorrowTransaction;
import com.library.management.entity.Member;
import com.library.management.service.BorrowTransactionService;
import com.library.management.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final BorrowTransactionService borrowTransactionService;

    // 1. Auth / Login
    @GetMapping("/")
    public String login() {
        return "auth/login"; 
    }

    // 2. Dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalBooks", bookService.getAllBooks().size());
        model.addAttribute("totalCategories", categoryService.getAllCategories().size());
        return "dashboard/dashboard";
    }

    // --- Admins Section ---
    @GetMapping("/admins")
    public String adminList() {
        return "admins/list";
    }

    @GetMapping("/admins/create")
    public String adminCreate() {
        return "admins/create";
    }

    @GetMapping("/admins/edit")
    public String adminEdit() {
        return "admins/edit";
    }

    // --- Books Section (HTML View Mappings) ---
    @GetMapping("/books")
    public String bookList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String status,
            Model model) {
        
        model.addAttribute("books", bookService.getAllBooks());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("selectedStatus", status != null ? status : "");
        
        return "books/list"; 
    }

    @GetMapping("/books/create")
    public String showCreateBookForm(Model model) {
        model.addAttribute("book", new com.library.management.entity.Book());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/create";
    }

    @PostMapping("/books/create")
    public String createBook(@ModelAttribute com.library.management.entity.Book book, RedirectAttributes redirectAttributes) {
        try {
            bookService.createBook(book);
            redirectAttributes.addFlashAttribute("successMessage", "Book created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/books";
    }

    @GetMapping("/books/edit/{id}")
    public String showEditBookForm(@PathVariable Integer id, Model model) {
        model.addAttribute("book", bookService.getBookById(id));
        model.addAttribute("categories", categoryService.getAllCategories());
        return "books/edit";
    }

    @PostMapping("/books/edit/{id}")
    public String updateBook(@PathVariable Integer id, @ModelAttribute com.library.management.entity.Book bookDetails, RedirectAttributes redirectAttributes) {
        try {
            bookService.updateBook(id, bookDetails);
            redirectAttributes.addFlashAttribute("successMessage", "Book updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/books";
    }

    @PostMapping("/books/delete/{id}")
    public String deleteBook(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/books";
    }

    // --- Categories Section ---
    @GetMapping("/categories")
    public String categoryList(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "categories/list";
    }

    @GetMapping("/categories/create")
    public String categoryCreate() {
        return "categories/create";
    }

    @PostMapping("/categories/create")
    public String createCategory(@ModelAttribute com.library.management.entity.Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.createCategory(category);
            redirectAttributes.addFlashAttribute("successMessage", "Category created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String categoryEdit(@PathVariable Integer id, Model model) {

        model.addAttribute("category", categoryService.getCategoryById(id));

        return "categories/edit";
    }

    @PostMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable Integer id, @ModelAttribute com.library.management.entity.Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.updateCategory(id, category);
            redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/categories";
    }

    @PostMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return "redirect:/categories";
    }

    // --- Members Section ---
    @GetMapping("/members")
    public String memberList(Model model) {
        model.addAttribute("members", memberService.getAllMembers());
        return "members/list";
    }

    @GetMapping("/members/create")
    public String memberCreate(Model model) {
        model.addAttribute("member", new Member());
        return "members/create";
    }

    @PostMapping("/members/create")
    public String createMember(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {
        try {
            memberService.createMember(member);
            redirectAttributes.addFlashAttribute("successMessage", "Member created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/members";
    }

    @GetMapping("/members/edit/{id}")
    public String memberEdit(@PathVariable Integer id, Model model) {
        model.addAttribute("member", memberService.getMemberById(id).orElse(null));
        return "members/edit";
    }

    @PostMapping("/members/edit/{id}")
    public String updateMember(@PathVariable Integer id, @ModelAttribute Member memberDetails, RedirectAttributes redirectAttributes) {
        try {
            memberService.updateMember(id, memberDetails);
            redirectAttributes.addFlashAttribute("successMessage", "Member updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/members";
    }

    @PostMapping("/members/delete/{id}")
    public String deleteMember(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            memberService.deleteMember(id);
            redirectAttributes.addFlashAttribute("successMessage", "Member deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/members";
    }

    // --- Transactions Section ---
    @GetMapping({"/transactions", "/transactions/list"})
    public String transactionList(Model model) {
        model.addAttribute("transactions", borrowTransactionService.getAllTransactions());
        return "transactions/list";
    }

    @GetMapping("/transactions/borrow")
    public String transactionBorrow(Model model) {
        model.addAttribute("transaction", new BorrowTransaction());
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("books", bookService.getAllBooks());
        return "transactions/borrow";
    }

    @PostMapping("/transactions/borrow")
    public String createTransaction(@ModelAttribute BorrowTransaction transaction, RedirectAttributes redirectAttributes) {
        try {
            borrowTransactionService.createTransaction(transaction);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction created successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/transactions";
    }

    @GetMapping("/transactions/return/{id}")
    public String returnTransaction(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            borrowTransactionService.returnBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Book returned successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/transactions";
    }

    // --- Transactions Section (အောက်ပါအတိုင်း /transactions ထည့်ပါ) ---

    @GetMapping("/transactions/detail/{id}")
    public String viewTransactionDetail(@PathVariable Integer id, Model model) {
        BorrowTransaction transaction = borrowTransactionService.getTransactionById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        model.addAttribute("transaction", transaction);
        return "transactions/detail"; 
    }

    @GetMapping("/transactions/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        BorrowTransaction transaction = borrowTransactionService.getTransactionById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        
        model.addAttribute("transaction", transaction);
        model.addAttribute("members", memberService.getAllMembers());
        model.addAttribute("books", bookService.getAllBooks());
        return "transactions/edit"; 
    }

    @PostMapping("/transactions/edit/{id}")
    public String updateTransaction(@PathVariable Integer id, @ModelAttribute BorrowTransaction transactionDetails, RedirectAttributes redirectAttributes) {
        try {
            borrowTransactionService.updateTransaction(id, transactionDetails);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/transactions";
    }

   
}