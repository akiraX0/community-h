package com.project.community.controller;

import com.project.community.dto.UserRequest;
import com.project.community.entities.User;
import com.project.community.repository.UserRepository;
import com.project.community.services.implementation.CustomUserDetails;
import com.project.community.services.interfaces.UserService;
import com.project.community.security.JwtUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        model.addAttribute("user", new UserRequest()); // Create an empty UserRequest object
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
    public String loginUser(@ModelAttribute("user") UserRequest user, Model model, HttpSession session) {
        try {
            // 1. Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            // 2. Set authentication to SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Load UserDetails and generate JWT
            UserDetails userDetails = customUserDetails.loadUserByUsername(user.getEmail());
            String jwt = jwtUtil.generateToken(userDetails.getUsername());


            // Storing the token in session
            session.setAttribute("jwt", jwt);

            // Add the JWT token to the model to pass it to the frontend
            model.addAttribute("jwt", jwt);
            log.info("Token: {}", jwt);

            // 4. Fetch the full User object from the database
            User loggedInUser = userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // 5. Log granted authorities
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                log.info("Granted authorities: {}", auth.getAuthorities());
            } else {
                log.warn("Authentication object is null — JWT not yet applied in SecurityContext");
            }

            // 6. Check role and redirect accordingly
            String role = loggedInUser.getRole().name();
            log.info("Role: {}", role);
            model.addAttribute("message", "Login successful!");

            if ("ORGANIZER".equalsIgnoreCase(role)) {

                return "admin_dashboard";
            } else {
                return "dashboard";
            }

        } catch (Exception e) {
            log.error("Login failed", e);
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }


}
