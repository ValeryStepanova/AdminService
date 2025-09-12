package com.example.AdminService.utils;

import com.example.AdminService.dto.UserPrincipal;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

@UtilityClass
public class CurrentUserService {

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            return new UserPrincipal(
                    UUID.fromString(jwt.getSubject()),
                    jwt.getClaim("preferred_username"),
                    jwt.getClaim("first_name"),
                    jwt.getClaim("last_name"),
                    jwt.getClaim("email"),
                    jwt.getClaimAsStringList("roles")
            );
        }

        return null;
    }
}
