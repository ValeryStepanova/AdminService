package com.example.AdminService.dto.program;

import jakarta.validation.constraints.NotBlank;

public record ProgramRequest(
    @NotBlank(message = "Program name is required")
    String name,

    @NotBlank(message = "Program description is required")
    String description
){
}
