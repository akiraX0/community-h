package com.project.community.services.implementation;

import com.project.community.dto.AuthenticationUtils;
import com.project.community.entities.Event;
import com.project.community.entities.Registration;
import com.project.community.entities.User;
import com.project.community.exceptions.ResourceNotFoundException;
import com.project.community.repository.EventRepository;
import com.project.community.repository.RegistrationRepository;
import com.project.community.services.interfaces.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private AuthenticationUtils authUtils;

    @Override
    public Registration registerUserForEvent(UUID eventId) {
        User volunteer = authUtils.getCurrentUser();
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        // Check if the user is already registered for this event
        Optional<Registration> existingRegistration = registrationRepository.findByVolunteerAndEvent(volunteer, event);
        if (existingRegistration.isPresent()) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        Registration registration = Registration.builder()
                .volunteer(volunteer)
                .event(event)
                .registrationDate(LocalDateTime.now())
                .build();

        try {
            return registrationRepository.save(registration);
        } catch (DataIntegrityViolationException e) {
            // Handle potential database-level unique constraint violations
            throw new IllegalStateException("Registration failed due to a data conflict.");
        }
    }

    @Override
    public Optional<Registration> getRegistration(Long id) {
        return registrationRepository.findById(id);
    }

    @Override
    public List<Registration> getAllRegistrations() {
        return registrationRepository.findAll();
    }

    @Override
    public List<Registration> getRegistrationsByUser(User user) {
        return registrationRepository.findByVolunteer(user);
    }

    @Override
    public List<Registration> getRegistrationsByEvent(Event event) {
        return registrationRepository.findByEvent(event);
    }
    @Override
    public List<Registration> getRegistrationsByEventId(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));
        return registrationRepository.findByEvent(event);
    }
}