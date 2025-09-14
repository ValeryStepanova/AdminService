package com.example.AdminService.client.config;

import com.example.AdminService.client.error_handler.AuthServiceErrorDecoder;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AuthServiceInternalFeignClientConfig {
    //private final AdminServiceApiClient adminServiceApiClient;
    private final AuthServiceErrorDecoder authServiceErrorDecoder;


//    @Bean("adminServiceRequestInterceptor")
//    public RequestInterceptor serviceRequestInterceptor() {
//        return requestTemplate -> {
//            try {
//                //String adminServiceAccessToken = adminServiceApiClient.getAdminServiceAccessToken();
//                String bearerToken = "Bearer " + adminServiceAccessToken;
//
//                log.info("Adding service token to internal request --> {}", bearerToken);
//                requestTemplate.header(HttpHeaders.AUTHORIZATION, bearerToken);
//            } catch (Exception e) {
//                log.error("Failed to add service token to request. Cause: {}", e.getMessage());
//                throw e;
//            }
//        };
//    }

    @Bean("auth-service-error-decoder")
    public ErrorDecoder errorDecoder() {
        return authServiceErrorDecoder;
    }
}
