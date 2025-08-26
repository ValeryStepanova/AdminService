package com.example.AdminService.repositories;

import com.example.AdminService.entities.ProfileDeletionAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileDeletionAuditLogRepository extends JpaRepository<ProfileDeletionAuditLog, Long>{
}
