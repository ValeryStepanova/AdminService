package com.example.AdminService.client;

import com.example.AdminService.dto.response.UserResponse;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.exception.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AuthServiceClientFallbackFactory implements FallbackFactory<AuthServiceClient> {
    @Override
    public AuthServiceClient create(Throwable cause) {
        log.error("Fallback triggered for AuthServiceClient. Cause: {}", cause.getMessage());

        return new AuthServiceClient() {
            @Override
            public UserResponse getUserById(UUID id) {
                // This fallback is mainly for network failures, circuit breaker, etc.
                log.error("Fallback: Error calling client-service for user {}: {}", id, cause.getMessage());

                if (cause instanceof java.net.ConnectException ||
                    cause instanceof java.net.SocketTimeoutException ||
                    cause.getMessage().contains("Connection refused")) {
                    log.error("Network error calling client-service for user {}: {}", id, cause.getMessage());
                    throw new ApiException(ResponseStatus.INTERNAL_SERVER_ERROR);
                }

                if (cause instanceof RuntimeException) {
                    throw (RuntimeException) cause;
                }

                throw new ApiException(ResponseStatus.INTERNAL_SERVER_ERROR);
            }
        };
    }
}
