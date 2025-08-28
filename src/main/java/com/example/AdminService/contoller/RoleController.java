package com.example.AdminService.contoller;

import com.example.AdminService.dto.request.AssignRoleRequest;
import com.example.AdminService.dto.UserReadDto;
import com.example.AdminService.service.impl.SupervisorServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("api/roles")
public class RoleController {

    private final SupervisorServiceImpl supervisorService;

    @PutMapping("/assign")
    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR')")
    public List<UserReadDto> assignExperts(@RequestBody AssignRoleRequest assignRoleRequest) {
        List<UserReadDto> users = supervisorService.getExistingUsers(assignRoleRequest.uuidList());

        return ResponseEntity.ok(supervisorService.assignRole(users, assignRoleRequest.role())).getBody();
    }
}
