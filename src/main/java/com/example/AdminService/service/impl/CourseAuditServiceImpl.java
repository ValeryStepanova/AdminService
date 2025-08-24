package com.example.AdminService.service.impl;

import com.example.AdminService.entities.CourseAudit;
import com.example.AdminService.repositories.CourseAuditRepository;
import com.example.AdminService.service.CourseAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CourseAuditServiceImpl implements CourseAuditService {

    private final CourseAuditRepository auditRepository;

    @Override
    public void auditChange(Long courseId, String action, String field,
                            String oldValue, String newValue, String performedBy) {
        CourseAudit audit = new CourseAudit();
        audit.setCourseId(courseId);
        audit.setAction(action);
        audit.setField(field);
        audit.setOldValue(oldValue);
        audit.setNewValue(newValue);
        audit.setPerformedBy(performedBy);
        audit.setPerformedAt(LocalDateTime.now());

        auditRepository.save(audit);
    }
}
