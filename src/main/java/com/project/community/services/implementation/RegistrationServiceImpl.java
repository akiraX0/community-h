package com.project.community.services.implementation;

import com.project.community.dto.EventsRequest;
import com.project.community.entities.Event;
import com.project.community.entities.Registration;
import com.project.community.entities.User;
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


    @Override
    public Registration registerUserForEvent(Long userId, UUID eventID) {
        return null;
    }

    @Override
    public Optional<Registration> getRegistration(Long id) {
        return Optional.empty();
    }

    @Override
    public List<Registration> getAllRegistrations() {
        return null;
    }

    @Override
    public List<Registration> getRegistrationsByUser(User user) {
        return null;
    }

    @Override
    public List<Registration> getRegistrationsByEvent(Event event) {
        return null;
    }
}