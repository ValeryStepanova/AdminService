package com.example.AdminService.service;

import java.util.UUID;

public interface KeycloakService {
    void updateUserRole(UUID uuid, String role);
}
