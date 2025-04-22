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
public class LoginController {

    @Autowired
    private AdminRepository adminRepository;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "registered", required = false) Boolean registered, Model model) {
        if (registered != null && registered) {
            model.addAttribute("success", "Registration successful! Please login.");
        }
        return "login_admin";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("Username") String username,
            @RequestParam("pass_admin") String password,
            Model model) {

        Admin admin = adminRepository.findByUsername(username);

        if (admin != null && admin.getPassword().equals(password)) {
            return "redirect:/admindashboard";
        } else {
            model.addAttribute("error", "Invalid username or password");
            return "login_admin";
        }
    }

    @GetMapping("/admindashboard")
    public String showAdminDashboard() {
        return "adminDashboard";
    }
}