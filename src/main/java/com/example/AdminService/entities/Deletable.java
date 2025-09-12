package com.example.AdminService.entities;

import java.util.UUID;

public interface Deletable {
    void softDelete(UUID currentUserId);
}
