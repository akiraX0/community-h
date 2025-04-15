package com.project.community.controller;

import com.project.community.dto.UserRequest;
import com.project.community.entities.User;
import com.project.community.repository.UserRepository;
import com.project.community.services.implementation.CustomUserDetails;
import com.project.community.services.interfaces.UserService;
import com.project.community.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@Slf4j
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetails customUserDetails;

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;


    // Show registration form for users
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserRequest());
        return "register"; // corresponds to register.html
    }

    // Handle user registration
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") @Valid UserRequest userRequest, Model model) {
        userService.UserRegister(userRequest);
        model.addAttribute("success", "User registered successfully!");
        return "login"; // redirect to login page after registration
    }

    // Show admin registration form (optional)
    @GetMapping("/adminRegister")
    public String showAdminRegisterForm(Model model) {
        model.addAttribute("user", new UserRequest());
        return "adminregister"; // corresponds to adminRegister.html
    }

    // Handle admin registration
    @PostMapping("/adminRegister")
    public String registerAdmin(@ModelAttribute("user") @Valid UserRequest adminRequest, Model model) {
        userService.AdminRegister(adminRequest);
        model.addAttribute("success", "Admin registered successfully!");
        return "login"; // redirect to login page after registration
    }

    // Show login form
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new UserRequest());
        return "login"; // corresponds to login.html
    }

    // Handle login
    @PostMapping("/login")
    public String loginUser(@ModelAttribute("user") UserRequest user, Model model) {
        try {
            // Authenticate the user with the provided credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            // Generate the JWT token
            UserDetails userDetails = customUserDetails.loadUserByUsername(user.getEmail());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());

            // Fetch the user from the repository
            User loggedInUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // Add the JWT to the model
            model.addAttribute("jwt", jwt);
            log.info("Token: {}", jwt);

            // Check the user's role and return the appropriate view
            if (loggedInUser.getRoles().get(0).equalsIgnoreCase("ORGANIZER")) {
                log.info("role: {}",loggedInUser.getRoles().get(0));
                model.addAttribute("message", "Login successful!");
                return "admin_dashboard"; // View name for organizer
            } else {
                model.addAttribute("message", "Login successful!");
                log.info("role: {}",loggedInUser.getRoles().get(0));

                return "dashboard"; // View for other users
            }

        } catch (Exception e) {
            log.error("Login failed", e);
            model.addAttribute("error", "Invalid email or password");
            return "login"; // Return to login page if login fails
        }
    }

}
