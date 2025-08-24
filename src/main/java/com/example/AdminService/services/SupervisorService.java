package com.example.AdminService.services;

import com.example.AdminService.dto.UserReadDto;
import com.example.AdminService.entities.User;
import com.example.AdminService.entities.enums.Role;
import com.example.AdminService.mapper.UserMapper;
import com.example.AdminService.repositories.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupervisorService {

    private final UserRepository userRepository;

    private final KeycloakService keycloakService;
    public Optional<UserReadDto> findByUUID(UUID uuid) {
        return Optional.ofNullable(UserMapper.INSTANCE.toDto(userRepository.findUserByUuid(uuid)));
    }

    public List<UserReadDto> assignRole(List<UserReadDto> users, String role) {
        List<UserReadDto> newExperts = new ArrayList<>();
        for (UserReadDto userReadDto : users) {
            User user = UserMapper.INSTANCE.toEntity(userReadDto);
            user.setRole(Role.valueOf(role));
            userRepository.save(user);
            //keycloakService.updateUserRole(user.getUuid(), Role.ROLE_EXPERT.name());
            newExperts.add(UserMapper.INSTANCE.toDto(userRepository.findUserByUuid(user.getUuid())));
        }
        return newExperts;
    }
}
