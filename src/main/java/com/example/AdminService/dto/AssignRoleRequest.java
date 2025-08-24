package com.example.AdminService.dto;

import java.util.List;
import java.util.UUID;

public record AssignRoleRequest(List<UUID> uuidList, String role) {
}
