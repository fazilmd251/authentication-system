package com.security.authentication.repository;

import com.security.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthRepository extends JpaRepository<User,Long> {
    boolean existsByEmail(String email);
}
