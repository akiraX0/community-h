package com.project.community.controller;

import com.project.community.dto.AuthenticationUtils;
import com.project.community.dto.EventResponse;
import com.project.community.dto.EventsRequest;
import com.project.community.dto.RegistrationResponse;
import com.project.community.entities.Registration;
import com.project.community.entities.User;
import com.project.community.services.interfaces.EventService;

import com.project.community.services.interfaces.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/events")
public class EventController {
    @Autowired
    private  EventService eventService;
    @Autowired
    private  AuthenticationUtils authUtils;
    @Autowired
    private RegistrationService registrationService;
    @GetMapping
    public String listAllEvents(Model model) {
        List<EventResponse> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "event_list";
    }

//    @GetMapping("/dashboard")
//    @PreAuthorize("hasAuthority('ORGANIZER')")
//    public String showAdminDashboard(Model model) {
//        List<EventResponse> events = eventService.getAllEvents();
//        model.addAttribute("events", events);
//        return "admin_dashboard";
//    }

    @GetMapping("/organizer/dashboard")
//    @PreAuthorize("hasAuthority('ORGANIZER')")
    public String showOrganizerDashboard(Model model) {
        String userEmail = authUtils.getCurrentUserEmail();
        List<EventResponse> events = eventService.getEventsByOrganizer(userEmail);
        model.addAttribute("events", events);
        return "admin_dashboard";
    }

    @GetMapping("/create")
//    @PreAuthorize("hasRole('ORGANIZER')")
    public String showCreateEventForm(Model model) {
        model.addAttribute("eventsRequest", new EventsRequest());
        return "create_event";
    }

    @PostMapping("/create")
//    @PreAuthorize("hasRole('ORGANIZER')")
    public String createEvent(@RequestBody @Valid EventsRequest eventsRequest,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "create_event";
        }

        try {
            EventResponse created = eventService.createEvent(eventsRequest, authUtils.getCurrentUserEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Event created successfully!");
            return "redirect:/events/organizer/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "create_event";
        }
    }

    @GetMapping("/{id}")
    public String getEventById(@PathVariable UUID id, Model model) {
        EventResponse event = eventService.getEventById(id);
        model.addAttribute("event", event);
        return "event_detail";
    }

    @GetMapping("/{id}/edit")
//    @PreAuthorize("hasAuthority('ORGANIZER')")
    public String showEditEventForm(@PathVariable UUID id, Model model) {
        EventResponse event = eventService.getEventById(id);

        // Populate form with current values
        EventsRequest eventsRequest = EventsRequest.builder()
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .build();

        model.addAttribute("eventId", id);
        model.addAttribute("eventsRequest", eventsRequest);
        return "edit_event";
    }

    @PostMapping("/{id}/edit")
//    @PreAuthorize("hasAuthority('ORGANIZER')")
    public String updateEvent(@PathVariable UUID id,
                              @ModelAttribute @Valid EventsRequest eventsRequest,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "edit_event";
        }

        try {
            EventResponse updated = eventService.updateEvent(id, eventsRequest, authUtils.getCurrentUserEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Event updated successfully!");
            return "redirect:/events/organizer/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/events/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
//    @PreAuthorize("hasAuthority('ORGANIZER')")
    public String deleteEvent(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id, authUtils.getCurrentUserEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/events/organizer/dashboard";
    }

    @GetMapping("/{eventId}/registrations")
//    @PreAuthorize("hasAuthority('ORGANIZER')") // Ensure only organizers can access this
    public String getEventRegistrations(@PathVariable UUID eventId, Model model, RedirectAttributes redirectAttributes) {
        EventResponse eventResponse = eventService.getEventById(eventId);

        if (eventResponse == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Event not found.");
            return "redirect:/events/organizer/dashboard"; // Or another appropriate page
        }

        User currentUser = authUtils.getCurrentUser();

        // Check if the current user is the organizer of this event
        if (!eventResponse.getOrganizerEmail().equals(currentUser.getEmail())) {
            redirectAttributes.addFlashAttribute("errorMessage", "You are not authorized to view registrations for this event.");
            return "redirect:/events/organizer/dashboard"; // Or another appropriate page
        }

        List<Registration> registrations = registrationService.getRegistrationsByEventId(eventId);
        List<RegistrationResponse> registrationResponses = registrations.stream()
                .map(registration -> RegistrationResponse.builder()
                        .registrationId(registration.getId())
                        .volunteerId(registration.getVolunteer().getId())
                        .volunteerEmail(registration.getVolunteer().getEmail())
                        .registrationDate(registration.getRegistrationDate())
                        .eventTitle(registration.getEvent().getTitle())
                        .eventId(registration.getEvent().getId())
                        .build())
                .collect(Collectors.toList());

        model.addAttribute("eventTitle", eventResponse.getTitle());
        model.addAttribute("registrations", registrationResponses);
        return "event_registrations"; // The name of your Thymeleaf template
    }
}