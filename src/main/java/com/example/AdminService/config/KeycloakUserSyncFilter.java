package com.example.AdminService.config;

import com.example.AdminService.entities.User;
import com.example.AdminService.entities.enums.Role;
import com.example.AdminService.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KeycloakUserSyncFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String keycloakId = jwt.getSubject();
            String username = jwt.getClaim("preferred_username");
            String email = jwt.getClaim("email");
            List<String> roles = jwt.getClaimAsStringList("roles");

            String currentRole = roles.stream()
                    .filter(role -> role.startsWith("ROLE_"))
                    .findFirst()
                    .orElse(null);

            UUID uuid = UUID.fromString(keycloakId);

            if (!userRepository.existsUserByUuid(uuid)) {
                User user = User.builder()
                        .uuid(uuid)
                        .username(username)
                        .email(email)
                        .role(Role.valueOf(currentRole))
                        .build();
                userRepository.save(user);
            }
        }

        filterChain.doFilter(request, response);

    }
}
