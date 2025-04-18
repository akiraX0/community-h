package com.project.community.services.interfaces;

import com.project.community.dto.EventsRequest;
import com.project.community.dto.EventResponse;
import com.project.community.entities.User;

import java.util.List;
import java.util.UUID;

public interface EventService {
    EventResponse createEvent(EventsRequest request, String email);
    List<EventResponse> getAllEvents();
    EventResponse getEventById(UUID id);
    EventResponse updateEvent(UUID id, EventsRequest request, String email);
    void deleteEvent(UUID id,String email);
    List<EventResponse> getEventsByOrganizer(String organizer);
}
