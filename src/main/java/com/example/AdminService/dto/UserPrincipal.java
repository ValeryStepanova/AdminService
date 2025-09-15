package com.example.AdminService.dto;

import com.itechart.profileserviceapi.enums.Role;

import java.util.List;
import java.util.UUID;

public record UserPrincipal(
        UUID uuid,
        String username,
        String firstName,
        String lastName,
        String email,
        List<String> roles
) {
}
