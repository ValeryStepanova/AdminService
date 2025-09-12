package com.example.AdminService.dto.task;

import jakarta.validation.constraints.NotBlank;

public record TaskUpdateRequest(
    @NotBlank(message = "Title is required")
    String title,

    @NotBlank(message = "Definition is required")
    String definition
) {
}
