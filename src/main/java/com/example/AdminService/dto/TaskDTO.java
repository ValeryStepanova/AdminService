package com.example.AdminService.dto;

import com.example.AdminService.entities.Program;
import com.example.AdminService.entities.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class TaskDTO {
    private Long id;

    private ProgramDTO program;

    private Long mentorId;

    private Long internId;

    private String title;

    private String githubLink;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;
}
