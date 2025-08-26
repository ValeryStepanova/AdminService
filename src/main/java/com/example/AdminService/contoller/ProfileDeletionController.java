package com.example.AdminService.contoller;

import com.example.AdminService.dto.response.ApiResponse;
import com.example.AdminService.entities.ProfileDeletionRequest;
import com.example.AdminService.service.ProfileDeletionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile-deletion")
@RequiredArgsConstructor
public class ProfileDeletionController {

    private final ProfileDeletionService deletionService;

    /**
     * Supervisor: удалить напрямую
     */
    @DeleteMapping("/users/{userUuid}")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ApiResponse<Void> deleteDirectly(@PathVariable UUID userUuid, Principal principal) {
        UUID supervisorUuid = UUID.fromString(principal.getName()); // предполагаем, что UUID хранится в subject
        deletionService.deleteProfileDirectly(userUuid, supervisorUuid);
        return ApiResponse.success(null, "User deleted directly by supervisor");
    }

    /**
     * Обычный пользователь: запрос на удаление
     */
    @PostMapping("/users/{userUuid}/request")
    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_INTERN','ROLE_EXPERT','ROLE_MENTOR')")
    public ApiResponse<ProfileDeletionRequest> requestDeletion(@PathVariable UUID userUuid, Principal principal) {
        UUID requestedBy = UUID.fromString(principal.getName());
        ProfileDeletionRequest request = deletionService.requestProfileDeletion(userUuid, requestedBy);
        return ApiResponse.success(request, "Deletion request created");
    }

    /**
     * Supervisor: получить список заявок
     */
    @GetMapping("/requests")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ApiResponse<List<ProfileDeletionRequest>> getRequests() {
        List<ProfileDeletionRequest> requests = deletionService.getPendingRequests();
        return ApiResponse.success(requests, "Pending requests fetched");
    }

    /**
     * Supervisor: утвердить заявку
     */
    @PostMapping("/requests/{id}/approve")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ApiResponse<ProfileDeletionRequest> approveRequest(@PathVariable Long id, Principal principal) {
        UUID supervisorUuid = UUID.fromString(principal.getName());
        ProfileDeletionRequest updated = deletionService.approveRequest(id, supervisorUuid);
        return ApiResponse.success(updated, "Request approved and user deleted");
    }

    /**
     * Supervisor: отклонить заявку
     */
    @PostMapping("/requests/{id}/reject")
    @PreAuthorize("hasRole('ROLE_SUPERVISOR')")
    public ApiResponse<ProfileDeletionRequest> rejectRequest(@PathVariable Long id, Principal principal) {
        UUID supervisorUuid = UUID.fromString(principal.getName());
        ProfileDeletionRequest updated = deletionService.rejectRequest(id, supervisorUuid);
        return ApiResponse.success(updated, "Request rejected");
    }
}

