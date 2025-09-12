package com.example.AdminService.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "course_audit")
public class CourseAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long courseId;
    private String action; // CREATED, UPDATED, DELETED
    private String field;  // name, description, mentorId, etc.
    private String oldValue;
    private String newValue;

    private String performedBy; // username or userId
    private LocalDateTime performedAt;
}