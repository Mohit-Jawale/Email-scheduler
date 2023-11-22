package com.example.emailscheduler.web;

import com.example.emailscheduler.payload.User;
import com.example.emailscheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;


@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/addUser")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "addUser";
    }

    @PostMapping("/addUser")
    public String addUser(@ModelAttribute User user) {
        userRepository.save(user);
        return "redirect:/addUser";
    }
    
    @GetMapping("/dropdown")
    public String showDropdown(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "dropdown"; // Name of your Thymeleaf template
    }
}