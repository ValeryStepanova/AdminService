package com.example.AdminService.repositories;

import com.example.AdminService.entities.CourseAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseAuditRepository extends JpaRepository<CourseAudit, Long> {
}