package com.example.AdminService.service.impl;

import com.example.AdminService.dto.UserReadDto;
import com.example.AdminService.entities.User;
import com.itechart.profileserviceapi.enums.Role;
import com.example.AdminService.exception.UserNotFoundException;
import com.example.AdminService.mapper.UserMapper;
import com.example.AdminService.repositories.UserRepository;
import com.example.AdminService.service.SupervisorService;
import com.example.AdminService.service.impl.KeycloakServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SupervisorServiceImpl implements SupervisorService {

    private final UserRepository userRepository;

    private final KeycloakServiceImpl keycloakService;

    public List<UserReadDto> getExistingUsers(List<UUID> uuids) {
        return uuids.stream()
                .map(uuid -> findByUUID(uuid)
                        .orElseThrow(() -> new UserNotFoundException("User with uuid is not found".formatted(uuid))))
                .toList();
    }

    public Optional<UserReadDto> findByUUID(UUID uuid) {
        return Optional.ofNullable(UserMapper.INSTANCE.toDto(userRepository.findUserByUuid(uuid)));
    }

    @Override
    public List<UserReadDto> assignRole(List<UserReadDto> users, String role) {
        return null;
    }
}
