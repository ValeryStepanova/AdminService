package com.example.AdminService.client;

import com.example.AdminService.client.config.AuthFeignClientConfig;
import com.example.AdminService.dto.profile.AccountCreationRequest;
import com.example.AdminService.dto.HttpResponse;
import com.example.AdminService.dto.profile.UserUpdateRequest;
import com.example.AdminService.enums.Role;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(
    name = "auth-service",
    configuration = AuthFeignClientConfig.class,
    fallbackFactory = AuthClientFallbackFactory.class
)
public interface AuthClient {

    @GetMapping("/api/v1/user")
    HttpResponse getUserById(@RequestParam("id") UUID id);

    @PostMapping("/api/v1/user/create")
    HttpResponse createAccount(@RequestBody @Valid AccountCreationRequest request);

    @PutMapping("/api/v1/user")
    HttpResponse updateAccount(@RequestParam("id") UUID id, @RequestBody @Valid UserUpdateRequest request);

    @PutMapping("/api/v1/user/role")
    HttpResponse updateRole(@RequestParam("id") UUID id, @RequestParam Role role);

    @DeleteMapping("/api/v1/user")
    HttpResponse deleteAccount(@RequestParam("id") UUID id);

}
