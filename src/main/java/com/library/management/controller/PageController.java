package com.library.management.controller;

import org.springframework.security.core.Authentication;

import java.time.LocalDate;

import com.library.management.dto.ProfileForm;
import com.library.management.dto.ResetPasswordForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.library.management.model.Role;
import com.library.management.security.UserPrincipal;
import com.library.management.dto.AdminEditForm;
import com.library.management.entity.User;
import com.library.management.service.BookService;
import com.library.management.service.CategoryService;
import com.library.management.dto.AdminForm;
import com.library.management.entity.BorrowTransaction;
import com.library.management.entity.Member;
import com.library.management.model.AccountStatus;
import com.library.management.repository.BookRepository;
import com.library.management.repository.BorrowTransactionRepository;
import com.library.management.repository.HoldRepository;
import com.library.management.repository.MemberRepository;
import com.library.management.service.BorrowTransactionService;
import com.library.management.service.MemberService;
import com.library.management.service.ReportService;
import com.library.management.service.UserService;
import  com.library.management.service.BookHistoryService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PageController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final MemberService memberService;
    private final BorrowTransactionService borrowTransactionService;
    private final BookHistoryService bookHistoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BorrowTransactionRepository transactionRepository;

    @Autowired
    private HoldRepository holdRepository;

    @Autowired
    private ReportService reportService;

    @GetMapping("/")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("loginError", "Login failed. Please check your credentials or account status.");
        }
        return "auth/login"; 
    }

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(value = "year", required = false) Integer year,
                            @RequestParam(value = "month", required = false) Integer month,
                            Model model) {

        LocalDate now = LocalDate.now();
        int selectedYear = (year != null) ? year : now.getYear();
        int selectedMonth = (month != null) ? month : now.getMonthValue();

        model.addAttribute("totalBooks", bookRepository.count());
        model.addAttribute("totalMembers", memberRepository.count());
        model.addAttribute("borrowedCount", transactionRepository.countByStatus("BORROWED"));
        model.addAttribute("overdueCount", transactionRepository.countByStatus("OVERDUE"));
        model.addAttribute("holdCount", holdRepository.countByStatus("PENDING"));

        double monthlyFine = reportService.getMonthlyTotalFine(selectedYear, selectedMonth);
        model.addAttribute("monthlyFine", monthlyFine);
        model.addAttribute("selectedYear", selectedYear);
        model.addAttribute("selectedMonth", selectedMonth);

        model.addAttribute("recentTransactions", transactionRepository.findTop5ByOrderByIdDesc());

        return "dashboard/dashboard";
    }
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
         if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal principal) {
            ProfileForm profileForm = new ProfileForm();
            profileForm.setId(principal.getId());
            profileForm.setName(principal.getName());
            profileForm.setUsername(principal.getUsername());
            profileForm.setEmail(principal.getEmail());
            // load role/status from the persisted user for accuracy
            userService.getUserById(principal.getId()).ifPresent(user -> {
                profileForm.setRole(user.getRole());
                profileForm.setStatus(user.getStatus());
            });
            model.addAttribute("profileForm", profileForm);
        }
        return "profile/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute ProfileForm profileForm,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to update profile.");
            return "redirect:/profile";
        }

        try {
            if (!principal.getId().equals(profileForm.getId())) {
                throw new IllegalArgumentException("You may only update your own profile.");
            }

            // Update only the username field (do not change name)
            User updated = userService.updateUserName(principal.getId(), profileForm.getUsername());

            // Refresh the SecurityContext so the session reflects the new username immediately
            com.library.management.security.UserPrincipal newPrincipal = new com.library.management.security.UserPrincipal(updated);
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken newAuth =
                    new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                            newPrincipal, newPrincipal.getPassword(), newPrincipal.getAuthorities());
            org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(newAuth);

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully.");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/profile";
        }
    }

    @GetMapping("/admin-management/reset-password/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String resetPasswordForm(@PathVariable Integer id, Model model) {
        User admin = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        ResetPasswordForm resetPasswordForm = new ResetPasswordForm();
        resetPasswordForm.setId(admin.getId());
        resetPasswordForm.setName(admin.getName());
        resetPasswordForm.setEmail(admin.getEmail());
        model.addAttribute("resetPasswordForm", resetPasswordForm);
        return "admins/reset-password";
    }

    @PostMapping("/admin-management/reset-password/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String resetPassword(@PathVariable Integer id, 
                                @ModelAttribute("resetPasswordForm") ResetPasswordForm form, 
                                RedirectAttributes redirectAttributes) {
        try {
     
            if (!form.getNewPassword().equals(form.getConfirmPassword())) {
                redirectAttributes.addFlashAttribute("errorMessage", "New password and confirm password do not match.");
                return "redirect:/admin-management/reset-password/" + id;
            }

            userService.resetPassword(id, form.getNewPassword());

            redirectAttributes.addFlashAttribute("successMessage", "Password has been reset successfully.");
            return "redirect:/admin-management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin-management/reset-password/" + id;
        }
    }

    // --- Admins Section ---
    @GetMapping("/admin-management")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String adminManagement(Model model) {
        model.addAttribute("admins", userService.getAllAdmins());
        return "admins/list";
    }

    @GetMapping("/admin-management/create")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String adminCreate(Model model) {
        model.addAttribute("adminForm", new AdminForm());
        return "admins/create";
    }

    @GetMapping("/admin-management/edit/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String adminEdit(@PathVariable Integer id, Model model) {
        User admin = userService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        AdminEditForm form = new AdminEditForm();
        form.setId(admin.getId());
        form.setName(admin.getName());
        form.setUsername(admin.getUsername());
        form.setEmail(admin.getEmail());
        form.setStatus(admin.getStatus());
        model.addAttribute("adminEditForm", form);
        return "admins/edit";
    }

    @PostMapping("/admin-management/create")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String createAdmin(@ModelAttribute AdminForm adminForm, RedirectAttributes redirectAttributes) {
        try {
            if (!adminForm.getPassword().equals(adminForm.getConfirmPassword())) {
                throw new IllegalArgumentException("Passwords do not match.");
            }
            if (userService.usernameExists(adminForm.getUsername())) {
                throw new IllegalArgumentException("Username is already taken.");
            }
            if (userService.emailExists(adminForm.getEmail())) {
                throw new IllegalArgumentException("Email is already registered.");
            }
            User newUser = new User();
            newUser.setName(adminForm.getName());
            newUser.setUsername(adminForm.getUsername());
            newUser.setEmail(adminForm.getEmail());
            newUser.setPassword(adminForm.getPassword());
            newUser.setStatus(adminForm.getStatus() != null ? adminForm.getStatus() : AccountStatus.ACTIVE);
            userService.createAdmin(newUser);
            redirectAttributes.addFlashAttribute("successMessage", "Admin account created successfully.");
            return "redirect:/admin-management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin-management/create";
        }
    }

    @PostMapping("/admin-management/edit/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String updateAdmin(@PathVariable Integer id, @ModelAttribute AdminEditForm adminEditForm, RedirectAttributes redirectAttributes) {
        try {
            User updatedAdmin = new User();
            updatedAdmin.setId(id);
            updatedAdmin.setName(adminEditForm.getName());
            updatedAdmin.setUsername(adminEditForm.getUsername());
            updatedAdmin.setEmail(adminEditForm.getEmail());
            updatedAdmin.setStatus(adminEditForm.getStatus());
            userService.updateAdmin(id, updatedAdmin);
            redirectAttributes.addFlashAttribute("successMessage", "Admin account updated successfully.");
            return "redirect:/admin-management";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin-management/edit/" + id;
        }
    }

    @PostMapping("/admin-management/disable/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String disableAdmin(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.disableAdmin(id);
            redirectAttributes.addFlashAttribute("successMessage", "Admin account disabled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin-management";
    }

    @PostMapping("/admin-management/enable/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String enableAdmin(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            userService.enableAdmin(id);
            redirectAttributes.addFlashAttribute("successMessage", "Admin account enabled.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin-management";
    }

    // Old Routes Redirects
    @GetMapping("/admins")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String redirectOldAdminList() {
        return "redirect:/admin-management";
    }

    @GetMapping("/admins/create")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String redirectOldAdminCreate() {
        return "redirect:/admin-management/create";
    }

    @GetMapping("/admins/edit")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public String redirectOldAdminEdit() {
        return "redirect:/admin-management";
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

    @GetMapping("/books/history/{id}")
    public String viewBookHistories(@PathVariable("id") Integer id, Model model) {
        model.addAttribute("histories", bookHistoryService.getHistoriesByBookId(id));
        return "books/history"; 
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

    @GetMapping("/members/details/{id}")
    public String memberDetails(@PathVariable Integer id, Model model) {
        Member member = memberService.getMemberById(id)
                .orElseThrow(() -> new RuntimeException("Member not found with id: " + id));
        model.addAttribute("member", member);
        return "members/details";
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

    @PostMapping("/return/{id}")
    public String returnBook(@PathVariable("id") Integer id, 
                             @RequestParam("status") String returnStatus, 
                             RedirectAttributes redirectAttributes) {
        try {
            borrowTransactionService.returnBook(id, returnStatus);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/transactions/list";
    }

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