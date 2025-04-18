package com.project.community.services.implementation;

import com.project.community.dto.EventResponse;
import com.project.community.dto.EventsRequest;
import com.project.community.entities.Event;
import com.project.community.entities.User;
import com.project.community.exceptions.ResourceNotFoundException;
import com.project.community.exceptions.UserNotFoundException;
import com.project.community.repository.EventRepository;
import com.project.community.repository.UserRepository;
import com.project.community.services.interfaces.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    @Override
    public EventResponse createEvent(EventsRequest request, String email) {
        log.info("Request - Title: {}, Description: {}", request.getTitle(), request.getDescription(),request.getEventDate(),request.getLocation(),request.getEventDate());
        log.info("email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Organizer with email " + email + " not found"));
        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .eventDate(request.getEventDate())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .organizer(user)
                .build();

        Event saved = eventRepository.save(event);
        log.info("savedEvent: {}", saved.getEventDate());
        log.info("savedEvent: {}", saved.getCreatedAt());
        log.info("savedEvent: {}", saved.getOrganizer());
        log.info("savedEvent: {}", saved.getEventDate());

        return toResponse(saved);
    }
    @Override
    public List<EventResponse> getEventsByOrganizer(String email) {
        // Get all events created by this organizer
        List<Event> events = eventRepository.findByOrganizerEmail(email);

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
    public EventResponse updateEvent(UUID id, EventsRequest request, String  email) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Organizer not Found"));
        // Optional: only allow update if same creator
        if (!event.getOrganizer().getId().equals(user.getId())) {
            throw new UserNotFoundException("Unauthorized to update this event. Only the organizer can update it.");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setLocation(request.getLocation());
        event.setUpdatedAt(LocalDateTime.now());
        Event updated = eventRepository.save(event);
        return toResponse(updated);
    }

    @Override
    public void deleteEvent(UUID id, String email) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Organizer not Found"));

        // Authorization check: Ensure the user deleting the event is the organizer who created it
        if (!event.getOrganizer().getId().equals(user.getId())) {
            throw new UserNotFoundException("Unauthorized to delete this event. Only the organizer can delete it.");
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
