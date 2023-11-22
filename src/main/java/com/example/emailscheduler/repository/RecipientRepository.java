package com.example.emailscheduler.repository;

import com.example.emailscheduler.payload.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {
    // Additional custom queries or methods can be defined here if needed

}
