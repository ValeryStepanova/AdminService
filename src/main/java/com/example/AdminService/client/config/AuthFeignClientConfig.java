package com.example.AdminService.client.config;

import com.example.AdminService.client.error_handler.AuthClientErrorDecoder;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.exception.ApiException;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthFeignClientConfig {
    private final AuthClientErrorDecoder authClientErrorDecoder;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Get current HTTP request (bound to thread)
            HttpServletRequest request =
                ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()))
                    .getRequest();

            String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
            log.info("Bearer Token --> {}", bearerToken);

            if (bearerToken == null)
                throw new ApiException(ResponseStatus.TOKEN_REQUIRED);

            if (!bearerToken.startsWith("Bearer "))
                throw new ApiException(ResponseStatus.TOKEN_MUST_START_WITH_BEARER);

            requestTemplate.header(HttpHeaders.AUTHORIZATION, bearerToken);
        };
    }

    @Bean("auth-client-error-decoder")
    public ErrorDecoder errorDecoder() {
        return authClientErrorDecoder;
    }

}
