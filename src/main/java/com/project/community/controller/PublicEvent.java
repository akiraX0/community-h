package com.project.community.controller;

import com.project.community.dto.EventResponse;
import com.project.community.services.interfaces.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api")
public class PublicEvent {

    @Autowired
    private EventService eventService;
    @GetMapping("/getEvents")
    public String listAllEvents(Model model) {
        List<EventResponse> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "event_list";
    }
}
