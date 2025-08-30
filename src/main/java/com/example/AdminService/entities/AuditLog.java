package com.example.AdminService.entities;

import com.example.AdminService.entities.enums.EntityOperations;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;
    private Long entityId;
    private String oldValue;
    private String newValue;
    @Enumerated(value = EnumType.STRING)
    private EntityOperations operation;
    private String updatedBy;
    private LocalDateTime updatedAt;
}
