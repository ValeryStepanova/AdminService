package com.example.AdminService.contoller;

import com.example.AdminService.dto.HttpResponse;
import com.example.AdminService.dto.profile.AccountCreationRequest;
import com.example.AdminService.dto.profile.UserUpdateRequest;
import com.example.AdminService.enums.Role;
import com.example.AdminService.service.impl.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;


    @GetMapping()
    public HttpEntity<HttpResponse> getById(@RequestParam("id") UUID id) {
        return ResponseEntity.ok(profileService.getAccountById(id));
    }

    @PostMapping("/create")
    public HttpEntity<HttpResponse> createAccount(@RequestBody @Valid AccountCreationRequest request) {
        return ResponseEntity.ok(profileService.createAccount(request));
    }

    @PutMapping()
    public HttpEntity<HttpResponse> updateAccount(@RequestParam("id") UUID id, @RequestBody @Valid UserUpdateRequest request) {
        return ResponseEntity.ok(profileService.updateAccountById(id, request));
    }

    @PutMapping("/role")
    public HttpEntity<HttpResponse> updateRole(@RequestParam("id") UUID id, @RequestParam Role role) {
        return ResponseEntity.ok(profileService.updateRoleById(id, role));
    }

    @DeleteMapping()
    public HttpEntity<HttpResponse> deleteAccount(@RequestParam("id") UUID id) {
        return ResponseEntity.ok(profileService.deleteAccountById(id));
    }
}
