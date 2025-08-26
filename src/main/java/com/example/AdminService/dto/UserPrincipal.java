package com.example.AdminService.dto;

import java.util.List;
import java.util.UUID;

public record UserPrincipal(
        UUID uuid,
        String username,
        String email,
        List<String> roles
) {
}
