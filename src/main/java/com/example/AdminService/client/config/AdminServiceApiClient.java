package com.example.AdminService.client.config;

import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminServiceApiClient {
   // private final RestTemplateUtils restTemplateUtils;

//    @Value("${keycloak.server-url}")
    private String keycloakUrl = "http://localhost:8080";
//    @Value("${keycloak.realm}")
    private String realm = "examinai";
//    @Value("${keycloak.admin-service.client-id}")
    private String clientId = "admin-service";
//    @Value("${keycloak.admin-service.client-secret}")
    private String clientSecret = "admin-secret";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String CLIENT_CREDENTIALS = "client_credentials";


//    public String getAdminServiceAccessToken() {
//        try {
//            String url = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);
//            String payload = String.format("client_id=%s&client_secret=%s&grant_type=%s", clientId, clientSecret, CLIENT_CREDENTIALS);
//            HttpHeaders headers = new HttpHeaders();
//            headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
//
//           // ResponseEntity<Map> response = restTemplateUtils.execute(url, payload, headers, HttpMethod.POST, Map.class);
//            if (response.getStatusCode().is2xxSuccessful())
//                return Objects.requireNonNull(response.getBody()).get(ACCESS_TOKEN).toString();
//            else
//                throw new ApiException(ResponseStatus.ERROR_GETTING_ACCESS_TOKEN);
//        } catch (Exception e) {
//            log.error("Failed to get admin access token. Cause: ", e);
//            throw e;
//        }
//    }
}

