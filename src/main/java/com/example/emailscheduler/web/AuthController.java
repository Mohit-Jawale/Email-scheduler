package com.example.emailscheduler.web;


import com.example.emailscheduler.payload.User;
import com.example.emailscheduler.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Assuming you have a login.html or login.jsp in your templates or webapp directory
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String email, @RequestParam String password, Model model) {
        // Retrieve the user from the database based on the provided email
        User user = userRepository.findByEmail(email);

        // Check if the user exists and the password matches
        if (user != null && user.getPassword().equals(password)) {
            // Authentication successful, you can redirect to a dashboard or home page
            //return "redirect:/composeEmail";
            return "dashboard";
        } else {
            // Authentication failed, add an error message to the model
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    // @GetMapping("/emailform")
    // public String showDashboard() {
    //     return "emailform"; // Assuming you have a dashboard.html or dashboard.jsp
    // }
}
