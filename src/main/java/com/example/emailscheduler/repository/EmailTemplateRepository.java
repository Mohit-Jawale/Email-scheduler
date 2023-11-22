package com.example.emailscheduler.repository;
import com.example.emailscheduler.payload.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
}
