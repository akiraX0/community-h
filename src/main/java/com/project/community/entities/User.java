package com.project.community.entities;

import com.project.community.dto.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;


    private UserRole role;

    @OneToMany(mappedBy = "volunteer")
    private List<Registration> registrations = new ArrayList<>();

    @OneToMany(mappedBy = "organizer")
    private List<Event> events = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
