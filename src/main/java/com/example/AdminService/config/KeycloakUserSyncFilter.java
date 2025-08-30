package com.example.AdminService.config;

import com.example.AdminService.entities.User;
import com.itechart.profileserviceapi.enums.Role;
import com.example.AdminService.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeycloakUserSyncFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String keycloakId = jwt.getSubject();
            String username = jwt.getClaim("preferred_username");
            List<String> roles = jwt.getClaimAsStringList("roles");

            List<Role> currentRoles = roles.stream()
                    .filter(role -> role!=null && role.startsWith("ROLE_"))
                    .map(Role::valueOf)
                    .toList();
            log.info("CURRENT_ROLES: "+currentRoles);
            UUID uuid = UUID.fromString(keycloakId);
            if (!userRepository.existsUserByUuid(uuid)) {
                User user = User.builder()
                        .uuid(uuid)
                        .username(username)
                        .roles(currentRoles)
                        .build();
                userRepository.save(user);
            }
        }
        log.info("USER WAS CREATE");
        filterChain.doFilter(request, response);

    }
}
