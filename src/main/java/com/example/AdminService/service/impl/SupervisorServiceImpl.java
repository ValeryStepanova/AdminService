package com.example.AdminService.service.impl;

import com.example.AdminService.dto.UserReadDto;
import com.example.AdminService.entities.User;
import com.example.AdminService.entities.enums.Role;
import com.example.AdminService.exception.UserNotFoundException;
import com.example.AdminService.mapper.UserMapper;
import com.example.AdminService.repositories.UserRepository;
import com.example.AdminService.service.SupervisorService;
import com.example.AdminService.service.impl.KeycloakServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    public List<UserReadDto> assignRole(List<UserReadDto> users, String role) {
        List<UserReadDto> newUsers = new ArrayList<>();
        for (UserReadDto userReadDto : users) {
            User user = UserMapper.INSTANCE.toEntity(userReadDto);
            user.setRole(Role.valueOf(role));
            userRepository.save(user);
            keycloakService.updateUserRole(user.getUuid(), role);
            newUsers.add(UserMapper.INSTANCE.toDto(userRepository.findUserByUuid(user.getUuid())));
        }
        return newUsers;
    }
}
