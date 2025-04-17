package com.project.community.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "registrations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", insertable = false, updatable = false)
    private User volunteer;

    @ManyToOne
    @JoinColumn(name = "eventId", insertable = false, updatable = false)
    private Event event;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
