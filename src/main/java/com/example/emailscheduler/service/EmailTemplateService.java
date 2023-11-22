package com.example.emailscheduler.service;

import com.example.emailscheduler.payload.EmailTemplate;
import com.example.emailscheduler.repository.EmailTemplateRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailTemplateService {
    private final EmailTemplateRepository templateRepository;

    public EmailTemplateService(EmailTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    public List<EmailTemplate> getAllTemplates() {
        return templateRepository.findAll();
    }

    public EmailTemplate getTemplateById(Long templateId) {
        return templateRepository.findById(templateId).orElse(null);
    }

    public List<EmailTemplate> findAll() {
        return templateRepository.findAll();

    }
}
