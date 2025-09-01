package com.example.AdminService.repositories;

import com.example.AdminService.entities.User;
import com.itechart.profileserviceapi.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUuid(UUID uuid);

    boolean existsUserByUuid(UUID uuid);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.id = :id AND :role MEMBER OF u.roles")
    boolean existsUserByIdAndRole(@Param("id") Long id, @Param("role") Role role);

}
