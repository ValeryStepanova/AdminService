package com.example.AdminService.repositories;

import com.example.AdminService.entities.ProfileDeletionRequest;
import com.example.AdminService.entities.ProfileDeletionRequest.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileDeletionRequestRepository extends JpaRepository<ProfileDeletionRequest, Long> {
    List<ProfileDeletionRequest> findByStatus(Status status);
}
