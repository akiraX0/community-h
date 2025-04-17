package com.project.community.repository;

import com.project.community.entities.Event;
import com.project.community.entities.Registration;
import com.project.community.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, Long> {

    // Check if a registration exists for a specific user and event
    Optional<Registration> findByVolunteerAndEvent(User volunteer, Event event);

    // Find all registrations for a specific user
    List<Registration> findByVolunteer(User volunteer);

    // Find all registrations for a specific event
    List<Registration> findByEvent(Event event);

    // You might also want a method to delete a registration
    void deleteByVolunteerAndEvent(User volunteer, Event event);
}