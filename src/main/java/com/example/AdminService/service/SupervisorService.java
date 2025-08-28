package com.example.AdminService.service;

import com.example.AdminService.dto.UserReadDto;

import java.util.List;

public interface SupervisorService {
    List<UserReadDto> assignRole(List<UserReadDto> users, String role);
}
