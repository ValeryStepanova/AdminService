package com.example.AdminService.client;

import com.example.AdminService.client.config.AuthServiceInternalFeignClientConfig;
import com.example.AdminService.dto.response.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(
    name = "auth-service-internal",
    url = "http://localhost:8070",
    configuration = AuthServiceInternalFeignClientConfig.class,
    fallbackFactory = AuthServiceClientFallbackFactory.class
)
public interface AuthServiceClient {

    @GetMapping("/api/v1/user/internal/id/{id}")
    UserResponse getUserById(@PathVariable UUID id);

}
