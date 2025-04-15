package com.project.community.services.interfaces;

import com.project.community.dto.EventsRequest;
import com.project.community.dto.EventResponse;
import com.project.community.entities.User;

import java.util.List;
import java.util.UUID;

public interface EventService {
    EventResponse createEvent(EventsRequest request, User user);
    List<EventResponse> getAllEvents();
    EventResponse getEventById(UUID id);
    EventResponse updateEvent(UUID id, EventsRequest request, User user);
    void deleteEvent(UUID id);
    List<EventResponse> getEventsByOrganizer(User organizer);
}
