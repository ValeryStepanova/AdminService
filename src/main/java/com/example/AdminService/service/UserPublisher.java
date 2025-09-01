package com.example.AdminService.service;

import com.example.AdminService.dto.event.UserUpdateEvent;

public interface UserPublisher {
    void publishRoleUpdate(UserUpdateEvent userUpdateEvent);
}
