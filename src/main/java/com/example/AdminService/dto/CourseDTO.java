package com.example.AdminService.dto;

import com.example.AdminService.entities.enums.CourseStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CourseDTO {

    private Long id;

    private String name;

    private Long supervisorId;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
