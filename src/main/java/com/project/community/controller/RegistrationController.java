package com.project.community.controller;

import com.project.community.dto.EventResponse;
import com.project.community.entities.Event;
import com.project.community.entities.User;
import com.project.community.repository.UserRepository;
import com.project.community.services.interfaces.EventService;
import com.project.community.services.interfaces.RegistrationService;
import com.project.community.services.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/register")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventService eventService;
    @PostMapping("/{eventId}/register")
    public String registerForEvent(@PathVariable UUID eventId, Authentication authentication, RedirectAttributes redirectAttributes) {
        String userEmail = authentication.getName();

        // Check if the event exists, and if the user is authenticated
        EventResponse event = eventService.getEventById(eventId);
        if (event == null || userEmail == null) {
            redirectAttributes.addFlashAttribute("registrationError", "Event or User not found.");
            return "redirect:/events/" + eventId;
        }

        try {
            registrationService.registerUserForEvent(userEmail, eventId);  // Registration service logic
            redirectAttributes.addFlashAttribute("registrationSuccess", "Successfully registered for the event.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("registrationError", "Registration failed: " + e.getMessage());
        }
        return "redirect:/events/" + eventId;
    }

}
