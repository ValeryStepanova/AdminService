package com.example.AdminService.dto.course;

import java.util.List;
import java.util.UUID;

public record UnassignUsersResponse(
        Long courseId,
        List<UUID> internIds,
        List<UUID> mentorIds
) {
}
