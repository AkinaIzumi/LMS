package com.library.management.repository;

import com.library.management.entity.User;
import com.library.management.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByRole(Role role);

    List<User> findAllByRole(Role role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}