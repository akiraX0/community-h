package com.project.community.controller;

import com.project.community.dto.EventResponse;
import com.project.community.dto.EventsRequest;
import com.project.community.entities.User;
import com.project.community.services.interfaces.EventService;
import com.project.community.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/events")
public class EventController {

    @Autowired
    private  UserRepository userRepository;
    @Autowired
    private  EventService eventService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAuthority('ORGANIZER')")
    public String showDashboard(Model model) {
        List<EventResponse> events = eventService.getAllEvents(); // your service method
        model.addAttribute("events", events);
        return "admin_dashboard";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('ORGANIZER')")
    public String showCreateEventForm(Model model) {
        model.addAttribute("eventsRequest", new EventsRequest());
        return "create_event";
    }

    @PreAuthorize("hasRole('ORGANIZER')")
    @PostMapping("/addEvent")
    public String createEvent(@RequestBody @Valid EventsRequest eventsRequest, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        EventResponse created = eventService.createEvent(eventsRequest, user);
        model.addAttribute("event", created);
        return "dashboard"; // thymeleaf template name
    }
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ORGANIZER')")
    public String showOrganizerDashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if authentication exists
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login"; // If not authenticated, redirect to login
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Continue with event fetching if user is valid
        List<EventResponse> events = eventService.getEventsByOrganizer(user);
        model.addAttribute("events", events);

        return "eventForm"; // Return the event form view
    }

    @GetMapping("/{id}")
    public String getEventById(@PathVariable UUID id, Model model) {
        EventResponse event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "eventDetail"; // thymeleaf template name
    }

    @PutMapping("/{id}/update")
    @PreAuthorize("hasRole('ORGANIZER')")
    public String updateEvent(@PathVariable UUID id, @ModelAttribute EventsRequest updatedRequest, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        EventResponse updated = eventService.updateEvent(id, updatedRequest, user);
        model.addAttribute("event", updated);
        return "eventUpdated"; // thymeleaf template name
    }

    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasRole('ORGANIZER')")
    public String deleteEvent(@PathVariable UUID id, Model model) {
        eventService.deleteEvent(id);
        return "redirect:/events"; // refresh event list
    }
}
