package com.project.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class RegistrationResponse {

    private Long registrationId;
    private Long volunteerId;
    private String volunteerEmail;
    private LocalDateTime registrationDate;
     private UUID eventId;
     private String eventTitle;
}