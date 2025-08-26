package com.example.AdminService.service.impl;

import com.example.AdminService.entities.ProfileDeletionAuditLog;
import com.example.AdminService.entities.ProfileDeletionRequest;
import com.example.AdminService.entities.ProfileDeletionRequest.Status;
import com.example.AdminService.entities.User;
import com.example.AdminService.repositories.ProfileDeletionAuditLogRepository;
import com.example.AdminService.repositories.ProfileDeletionRequestRepository;
import com.example.AdminService.repositories.UserRepository;
import com.example.AdminService.service.ProfileDeletionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileDeletionServiceImpl implements ProfileDeletionService {

    private final UserRepository userRepository;
    private final ProfileDeletionRequestRepository requestRepository;
    private final ProfileDeletionAuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public void deleteProfileDirectly(UUID targetUserUuid, UUID supervisorUuid) {
        User user = userRepository.findByUuid(targetUserUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(user);

        saveAudit(supervisorUuid, "DELETE_PROFILE", targetUserUuid, "Direct supervisor deletion");
        log.info("Supervisor {} deleted user {}", supervisorUuid, targetUserUuid);
    }

    @Override
    @Transactional
    public ProfileDeletionRequest requestProfileDeletion(UUID targetUserUuid, UUID requestedBy) {
        userRepository.findByUuid(targetUserUuid)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        ProfileDeletionRequest request = ProfileDeletionRequest.builder()
                .userUuid(targetUserUuid)
                .requestedBy(requestedBy)
                .status(Status.PENDING)
                .build();

        ProfileDeletionRequest saved = requestRepository.save(request);
        saveAudit(requestedBy, "REQUEST_DELETION", targetUserUuid, "User requested deletion");
        log.info("User {} requested deletion of profile {}", requestedBy, targetUserUuid);

        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfileDeletionRequest> getPendingRequests() {
        return requestRepository.findByStatus(Status.PENDING);
    }

    @Override
    @Transactional
    public ProfileDeletionRequest approveRequest(Long requestId, UUID supervisorUuid) {
        ProfileDeletionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (request.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Request already processed");
        }

        User user = userRepository.findByUuid(request.getUserUuid())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userRepository.delete(user);

        request.setStatus(Status.APPROVED);
        ProfileDeletionRequest saved = requestRepository.save(request);

        saveAudit(supervisorUuid, "APPROVE_REQUEST", request.getUserUuid(), "Supervisor approved deletion");
        log.info("Supervisor {} approved deletion of user {}", supervisorUuid, request.getUserUuid());

        return saved;
    }

    @Override
    @Transactional
    public ProfileDeletionRequest rejectRequest(Long requestId, UUID supervisorUuid) {
        ProfileDeletionRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Request not found"));

        if (request.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Request already processed");
        }

        request.setStatus(Status.REJECTED);
        ProfileDeletionRequest saved = requestRepository.save(request);

        saveAudit(supervisorUuid, "REJECT_REQUEST", request.getUserUuid(), "Supervisor rejected deletion");
        log.info("Supervisor {} rejected deletion of user {}", supervisorUuid, request.getUserUuid());

        return saved;
    }

    private void saveAudit(UUID actor, String action, UUID target, String details) {
        auditLogRepository.save(ProfileDeletionAuditLog.builder()
                .supervisorUuid(actor)
                .action(action)
                .userUuid(target)
                .details(details)
                .build());
    }
}
