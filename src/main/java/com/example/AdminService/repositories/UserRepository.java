package com.example.AdminService.repositories;

import com.example.AdminService.entities.User;
import com.example.AdminService.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByIdAndRole(Long id, Role role);
}