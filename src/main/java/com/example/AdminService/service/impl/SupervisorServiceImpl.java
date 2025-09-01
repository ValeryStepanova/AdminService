package com.example.AdminService.service.impl;

import com.example.AdminService.dto.UserReadDto;
import com.example.AdminService.dto.event.UserUpdateEvent;
import com.example.AdminService.entities.User;
import com.itechart.profileserviceapi.dto.UserDto;
import com.itechart.profileserviceapi.enums.Role;
import com.example.AdminService.exception.UserNotFoundException;
import com.example.AdminService.mapper.UserMapper;
import com.example.AdminService.repositories.UserRepository;
import com.example.AdminService.service.SupervisorService;
import com.example.AdminService.service.impl.KeycloakServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupervisorServiceImpl implements SupervisorService {

    private final UserRepository userRepository;

    private final KeycloakServiceImpl keycloakService;
    private final RabbitTemplate rabbitTemplate;

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
            List<Role> roles = user.getRoles();
            roles.add(Role.valueOf(role));
            user.setRoles(roles);
            userRepository.save(user);

            keycloakService.updateUserRole(user.getUuid(), role);

            User updatedUser = userRepository.findUserByUuid(user.getUuid());
            UserReadDto updatedDto = UserMapper.INSTANCE.toDto(updatedUser);
            newUsers.add(updatedDto);

            UserUpdateEvent event = new UserUpdateEvent(
                    updatedUser.getUuid(),
                    updatedUser.getRoles(),
                    LocalDateTime.now()
            );
            log.info("UpdateUserEvent created");
            rabbitTemplate.convertAndSend("user.role.exchange", "user.role.updated", event);
            log.info("Event sent");
        }

        return newUsers;
    }
}
