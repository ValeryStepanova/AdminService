package com.example.AdminService.client;

import com.example.AdminService.dto.profile.AccountCreationRequest;
import com.example.AdminService.dto.HttpResponse;
import com.example.AdminService.dto.profile.UserUpdateRequest;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.enums.Role;
import com.example.AdminService.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthClientFallbackFactory implements FallbackFactory<AuthClient> {
    @Override
    public AuthClient create(Throwable cause) {
        return new AuthClient() {

            @Override
            public HttpResponse getUserById(UUID id) {
                log.error("Fallback: Error calling client-service for getting account by id: {}: {}", id, cause.getMessage());
                return processError(cause);
            }

            @Override
            public HttpResponse createAccount(AccountCreationRequest request) {
                log.error("Fallback: Error calling client-service for account creation: {}: {}", request, cause.getMessage());
                return processError(cause);
            }

            @Override
            public HttpResponse updateAccount(UUID id, UserUpdateRequest request) {
                log.error("Fallback: Error calling client-service for updating account: {}: {}", request, cause.getMessage());
                return processError(cause);
            }

            @Override
            public HttpResponse updateRole(UUID id, Role role) {
                log.error("Fallback: Error calling client-service for setting role for account with id: {}: {}", id, cause.getMessage());
                return processError(cause);
            }

            @Override
            public HttpResponse deleteAccount(UUID id) {
                log.error("Fallback: Error calling client-service for deleting account with id: {}: {}", id, cause.getMessage());
                return processError(cause);
            }

            private HttpResponse processError(Throwable cause) {
                if (cause instanceof java.net.ConnectException ||
                    cause instanceof java.net.SocketTimeoutException ||
                    cause.getMessage().contains("Connection refused")) {
                    log.error("Network error calling client-service: {}", cause.getMessage());
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
