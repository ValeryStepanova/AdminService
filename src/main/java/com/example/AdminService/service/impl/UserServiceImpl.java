package com.example.AdminService.service.impl;

import com.example.AdminService.dto.response.AssignUsersResponse;
import com.example.AdminService.entities.User;
import com.itechart.profileserviceapi.dto.UserIdsRequest;
import com.itechart.profileserviceapi.enums.Role;
import com.example.AdminService.exception.UserNotFoundException;
import com.example.AdminService.repositories.UserRepository;
import com.example.AdminService.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public boolean existsByIdAndRole(Long id, Role role) {
        return userRepository.existsUserByIdAndRole(id, role);
    }

    @Override
    public User findById(Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElseThrow(() -> new UserNotFoundException("User with id '%d' not found".formatted(id)));
    }
}
