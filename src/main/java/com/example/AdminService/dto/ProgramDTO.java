package com.example.AdminService.dto;

import com.example.AdminService.dto.request.CourseCreateRequestDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgramDTO {
    private Long id;

    private CourseCreateRequestDTO course;

    private Long expertId;

    private String description;

    private Boolean approved = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
