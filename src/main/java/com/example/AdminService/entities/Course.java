package com.example.AdminService.entities;

import com.example.AdminService.entities.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "courses")
@Entity
public class Course {
    @Id
    @SequenceGenerator(name = "courses_id_seq", sequenceName = "courses_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "courses_id_seq")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "supervisor_id", nullable = false)
    private UUID supervisorId;

    @ElementCollection
    @CollectionTable(
            name = "course_mentors",
            joinColumns = @JoinColumn(name = "course_id")
    )
    @Column(name = "mentor_id", nullable = false)
    private Set<UUID> mentorIds = new HashSet<>();

    @ElementCollection
    @CollectionTable(
            name = "course_interns",
            joinColumns = @JoinColumn(name = "course_id")
    )
    @Column(name = "intern_id", nullable = false)
    private Set<UUID> internIds = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CourseStatus status;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
