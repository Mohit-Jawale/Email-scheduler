package com.example.emailscheduler.repository;

import com.example.emailscheduler.payload.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    // Additional custom queries or methods can be defined here if needed
    User findByEmail(String email);
}
