package com.example.AdminService.controllers;

import com.example.AdminService.dto.AssignRoleRequest;
import com.example.AdminService.dto.UserReadDto;
import com.example.AdminService.services.SupervisorService;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {

    private final SupervisorService supervisorService;
    @PutMapping("/experts")
    public List<UserReadDto> assignExperts(@RequestBody AssignRoleRequest assignRoleRequest){
        List<UserReadDto> users = new ArrayList<>();
        for (UUID uuid: assignRoleRequest.uuidList()) {
           users.add(supervisorService.findByUUID(uuid).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with uuid " + uuid + " not found")));
        }
        return ResponseEntity.ok(supervisorService.assignRole(users, assignRoleRequest.role())).getBody();
    }

}
