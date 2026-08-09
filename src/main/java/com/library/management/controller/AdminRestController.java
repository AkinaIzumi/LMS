package com.library.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.library.management.dto.AdminEditForm;
import com.library.management.dto.AdminForm;
import com.library.management.entity.User;
import com.library.management.model.AccountStatus;
import com.library.management.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin-management")
@RequiredArgsConstructor
public class AdminRestController {

    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public ResponseEntity<?> createAdmin(@RequestBody AdminForm adminForm) {
        try {
            if (!adminForm.getPassword().equals(adminForm.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("Passwords do not match.");
            }
            if (userService.usernameExists(adminForm.getUsername())) {
                return ResponseEntity.badRequest().body("Username is already taken.");
            }
            if (userService.emailExists(adminForm.getEmail())) {
                return ResponseEntity.badRequest().body("Email is already registered.");
            }

            User newUser = new User();
            newUser.setName(adminForm.getName());
            newUser.setUsername(adminForm.getUsername());
            newUser.setEmail(adminForm.getEmail());
            newUser.setPassword(adminForm.getPassword());
            newUser.setStatus(adminForm.getStatus() != null ? adminForm.getStatus() : AccountStatus.ACTIVE);
            
            userService.createAdmin(newUser);
            return ResponseEntity.ok("Admin account created successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('MAIN_ADMIN')")
    public ResponseEntity<?> updateAdmin(@PathVariable Integer id, @RequestBody AdminEditForm adminEditForm) {
        try {
            User updatedAdmin = new User();
            updatedAdmin.setId(id);
            updatedAdmin.setName(adminEditForm.getName());
            updatedAdmin.setUsername(adminEditForm.getUsername());
            updatedAdmin.setEmail(adminEditForm.getEmail());
            updatedAdmin.setStatus(adminEditForm.getStatus());
            
            userService.updateAdmin(id, updatedAdmin);
            return ResponseEntity.ok("Admin account updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}