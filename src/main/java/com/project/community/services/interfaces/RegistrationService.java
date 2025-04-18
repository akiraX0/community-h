package com.project.community.services.interfaces;

import com.project.community.entities.Event;
import com.project.community.entities.Registration;
import com.project.community.entities.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RegistrationService {

    Registration registerUserForEvent(UUID eventId);

    Optional<Registration> getRegistration(Long id);

    List<Registration> getAllRegistrations();

    List<Registration> getRegistrationsByUser(User user);

    List<Registration> getRegistrationsByEvent(Event event);

    List<Registration> getRegistrationsByEventId(UUID eventId);
}