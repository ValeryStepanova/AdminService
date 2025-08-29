package com.example.AdminService.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignSecurityConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            String token = getToken(); // Получи токен из SecurityContext или хранилища
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }

    private String getToken() {
        // Пример: получить токен текущего пользователя
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getTokenValue();
        }
        return "";
    }
}
