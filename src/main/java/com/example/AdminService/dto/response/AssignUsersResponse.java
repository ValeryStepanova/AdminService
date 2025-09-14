package com.example.AdminService.dto.response;

import java.util.List;
import java.util.UUID;

public record AssignUsersResponse(
        Long courseId,
        String courseName,
        List<UUID> mentors,
        List<UUID> interns
) {
}
