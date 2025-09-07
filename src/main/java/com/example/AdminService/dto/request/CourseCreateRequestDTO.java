package com.example.AdminService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CourseCreateRequestDTO {

    @NotBlank(message = "course name cannot be blank")
    private String name;
    @NotBlank(message = "course name cannot be blank")
    private String description;
}
