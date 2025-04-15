package com.project.community.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventsRequest {
    private String title;
    private String description;
    private String location;
    private LocalDateTime eventDate;
}
