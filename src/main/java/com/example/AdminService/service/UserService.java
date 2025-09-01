package com.example.AdminService.service;

import com.example.AdminService.entities.User;
import com.itechart.profileserviceapi.enums.Role;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserService {
    boolean existsByIdAndRole(Long id, Role role);

    User findById(Long id);
}
