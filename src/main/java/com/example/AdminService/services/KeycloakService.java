package com.example.AdminService.services;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KeycloakService {

//    private final Keycloak keycloak;
//    private final KeycloakProperties keycloakProperties;
//    public void updateUserRole(UUID uuid, String newRoleName) {
//        UserResource userResource = keycloak.realm(keycloakProperties.getRealm())
//                .users().get(uuid.toString());
//        RoleRepresentation role = keycloak.realm(keycloakProperties.getRealm())
//                .roles().get(newRoleName).toRepresentation();
//        userResource.roles().realmLevel().add(List.of(role));
//
//        System.out.println("Role '" + newRoleName + "' assigned to user: " + uuid);
//    }
}
