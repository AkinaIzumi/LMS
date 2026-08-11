package com.library.management.service.serviceImpl;

import com.library.management.entity.User;
import com.library.management.model.AccountStatus;
import com.library.management.model.Role;
import com.library.management.repository.UserRepository;
import com.library.management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllAdmins() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Integer id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User createAdmin(User user) {
        if (usernameExists(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (emailExists(user.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        user.setRole(Role.ADMIN);
        user.setStatus(AccountStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User updateAdmin(Integer id, User updatedAdmin) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));

        if (!existing.getUsername().equals(updatedAdmin.getUsername()) && usernameExists(updatedAdmin.getUsername())) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (!existing.getEmail().equals(updatedAdmin.getEmail()) && emailExists(updatedAdmin.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        existing.setName(updatedAdmin.getName());
        existing.setUsername(updatedAdmin.getUsername());
        existing.setEmail(updatedAdmin.getEmail());
        existing.setStatus(updatedAdmin.getStatus());

        return userRepository.save(existing);
    }

    @Override
    public void disableAdmin(Integer id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));

        if (existing.getRole() == Role.MAIN_ADMIN) {
            throw new IllegalArgumentException("The main admin cannot be disabled.");
        }

        existing.setStatus(AccountStatus.INACTIVE);
        userRepository.save(existing);
    }

    @Override
    public void enableAdmin(Integer id) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found."));

        if (existing.getRole() == Role.MAIN_ADMIN) {
            throw new IllegalArgumentException("The main admin account is always active.");
        }

        existing.setStatus(AccountStatus.ACTIVE);
        userRepository.save(existing);
    }

    @Override
    public Optional<User> findMainAdmin() {
        return userRepository.findByRole(Role.MAIN_ADMIN);
    }

    @Override
    public User createMainAdmin(String name, String username, String email, String rawPassword) {
        if (findMainAdmin().isPresent()) {
            throw new IllegalStateException("Main admin already exists.");
        }
        if (usernameExists(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }
        if (emailExists(email)) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        User admin = new User(name, username, email, passwordEncoder.encode(rawPassword), Role.MAIN_ADMIN, AccountStatus.ACTIVE);
        return userRepository.save(admin);
    }

    @Override
    public User resetMainAdmin(String name, String username, String email, String rawPassword) {
        userRepository.findAllByRole(Role.MAIN_ADMIN).forEach(userRepository::delete);
        userRepository.findByUsername(username).ifPresent(userRepository::delete);
        userRepository.findByEmail(email).ifPresent(userRepository::delete);
        User admin = new User(name, username, email, passwordEncoder.encode(rawPassword), Role.MAIN_ADMIN, AccountStatus.ACTIVE);
        return userRepository.save(admin);
    }

    @Override
    public User resetPassword(Integer id, String rawPassword) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        existing.setPassword(passwordEncoder.encode(rawPassword));
        return userRepository.save(existing);
    }

    @Override
    public User updateUserName(Integer id, String username) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!existing.getUsername().equals(username) && usernameExists(username)) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        existing.setUsername(username);
        return userRepository.save(existing);
    }
}