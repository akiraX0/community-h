package com.project.community.controller;

import com.project.community.services.interfaces.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/register")
public class EventRegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping("/{eventId}/register")
    public String registerForEvent(@PathVariable UUID eventId, RedirectAttributes redirectAttributes) {
        try {
            registrationService.registerUserForEvent(eventId);
            redirectAttributes.addFlashAttribute("registrationSuccess", "Successfully registered for the event.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("registrationError", "Registration failed: " + e.getMessage());
        }
        return "redirect:/events/" + eventId;
    }
}