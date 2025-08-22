package com.example.AdminService.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgramDTO {
    private Long id;

    private CourseDTO course;

    private Long expertId;

    private String description;

    private Boolean approved = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
