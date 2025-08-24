package com.example.AdminService.service;

public interface CourseAuditService {
    void auditChange(Long courseId, String action, String field,
                     String oldValue, String newValue, String performedBy);
}
