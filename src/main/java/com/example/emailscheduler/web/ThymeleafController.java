package com.example.emailscheduler.web;

import com.example.emailscheduler.payload.EmailRequest;
import com.example.emailscheduler.quartz.job.EmailJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ThymeleafController{
//
//    @Autowired
//    private EmailService emailService; // Assuming you have an EmailService to send emails
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);
    @GetMapping("/composeEmail")
    public String showEmailForm(Model model) {
        model.addAttribute("emailForm", new EmailRequest());
        return "emailForm";
    }


}
