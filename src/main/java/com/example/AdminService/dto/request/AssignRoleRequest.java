package com.example.AdminService.dto.request;

import java.util.List;
import java.util.UUID;

public record AssignRoleRequest(List<UUID> uuidList, String role) {
}
