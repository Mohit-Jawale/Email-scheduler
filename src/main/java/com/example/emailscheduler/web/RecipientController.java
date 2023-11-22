package com.example.emailscheduler.web;
import com.example.emailscheduler.payload.Recipient;
import com.example.emailscheduler.repository.RecipientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RecipientController {
    
    @Autowired
    private RecipientRepository recipientRepository;

    @GetMapping("/addRecipient")
    public String showAddUserForm(Model model) {
        model.addAttribute("recipient", new Recipient());
        return "addRecipient";
    }

    @PostMapping("/addRecipient")
    public String addUser(@ModelAttribute Recipient recipient) {
        recipientRepository.save(recipient);
        return "redirect:/addRecipient";
    }

}