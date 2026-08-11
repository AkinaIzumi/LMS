package com.library.management.service;

import com.library.management.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> getAllAdmins();

    Optional<User> getUserById(Integer id);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean usernameExists(String username);

    boolean emailExists(String email);

    User createAdmin(User user);

    User updateAdmin(Integer id, User updatedAdmin);

    void disableAdmin(Integer id);

    void enableAdmin(Integer id);

    Optional<User> findMainAdmin();

    User createMainAdmin(String name, String username, String email, String rawPassword);
    
    User resetMainAdmin(String name, String username, String email, String rawPassword);

    User updateUserName(Integer id, String name);

    User resetPassword(Integer id, String rawPassword);
}
