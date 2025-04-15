package com.project.community.services.implementation;

import com.project.community.dto.EventResponse;
import com.project.community.dto.EventsRequest;
import com.project.community.entities.Event;
import com.project.community.entities.User;
import com.project.community.exceptions.ResourceNotFoundException;
import com.project.community.exceptions.UserNotFoundException;
import com.project.community.repository.EventRepository;
import com.project.community.services.interfaces.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    @Override
    public EventResponse createEvent(EventsRequest request, User user) {
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .organizer(user)
                .build();

        Event saved = eventRepository.save(event);
        return toResponse(saved);
    }
    @Override
    public List<EventResponse> getEventsByOrganizer(User organizer) {
        // Get all events created by this organizer
        List<Event> events = eventRepository.findByOrganizerId(organizer.getId());

        // Convert each event to EventResponse
        List<EventResponse> responseList = new ArrayList<>();
        for (Event event : events) {
            EventResponse response = EventResponse.builder()
                    .id(event.getId())
                    .title(event.getTitle())
                    .description(event.getDescription())
                    .eventDate(event.getEventDate())
                    .location(event.getLocation())
                    .build();
            responseList.add(response);
        }

        return responseList;
    }

    @Override
    public List<EventResponse> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EventResponse getEventById(UUID id) {
        return eventRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
    }

    @Override
    public EventResponse updateEvent(UUID id, EventsRequest request, User user) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Optional: only allow update if same creator
        if (!event.getId().equals(user.getId())) {
            throw new UserNotFoundException("Unauthorized to update this event");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());

        Event updated = eventRepository.save(event);
        return toResponse(updated);
    }

    @Override
    public void deleteEvent(UUID id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found with id: " + id);
        }
        eventRepository.deleteById(id);
    }


    private EventResponse toResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .organizerEmail(event.getOrganizer().getEmail())
                .build();
    }
}
