package com.example.AdminService.repositories;

import com.example.AdminService.entities.User;
import com.example.AdminService.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUuid(UUID uuid);

    boolean existsUserByUuid(UUID uuid);

    boolean existsUserByIdAndRole(Long id, Role role);

    Optional<User> findByUuid(UUID uuid);
}
