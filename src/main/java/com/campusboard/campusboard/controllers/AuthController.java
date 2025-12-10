package com.campusboard.campusboard.controllers;

import com.campusboard.campusboard.models.User;
import com.campusboard.campusboard.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    // =============================
    // LOGIN PAGE
    // =============================
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    // =============================
    // REGISTRATION FORM
    // =============================
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // =============================
    // HANDLE REGISTRATION
    // =============================
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            userService.registerUser(user);
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }

        return "redirect:/login?registered";
    }

    // =============================
    // AFTER LOGIN SUCCESS
    // Redirect user to proper dashboard
    // =============================
    @GetMapping("/")
    public String homeRedirect(Authentication auth) {

        if (auth == null) {
            return "redirect:/login";
        }

        String role = auth.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else if (role.equals("ROLE_EMPLOYER")) {
            return "redirect:/employer/dashboard";
        } else {
            return "redirect:/student/dashboard";
        }
    }
}
