package com.project.community.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private UUID id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDate;
    private String organizerEmail;
}
