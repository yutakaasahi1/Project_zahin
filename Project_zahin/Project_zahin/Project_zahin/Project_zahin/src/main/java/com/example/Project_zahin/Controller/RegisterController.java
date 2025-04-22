package com.example.Project_zahin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.Project_zahin.models.Admin;
import com.example.Project_zahin.repository.AdminRepository;

@Controller
public class RegisterController {

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/users/register")
    public String processRegistration(
            @RequestParam("name") String name,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            Model model) {

        // Check if username already exists
        if (adminRepository.findByUsername(username) != null) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }

        // Create new admin
        Admin admin = new Admin();
        admin.setName(name);
        admin.setUsername(username);
        admin.setPassword(password); // Storing plain text password (not recommended for production)

        adminRepository.save(admin);

        return "redirect:/login?registered=true";
    }
}