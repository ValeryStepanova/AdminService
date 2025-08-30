package com.example.AdminService.entities;

import com.example.AdminService.entities.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    private String username;
    @Column(name="phone")
    private String phoneNumber;
    private String city;
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
}
