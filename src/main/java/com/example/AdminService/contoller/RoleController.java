package com.example.AdminService.contoller;

import com.itechart.profileserviceapi.api.UserClient;
import com.itechart.profileserviceapi.dto.AssignRoleRequest;
import com.itechart.profileserviceapi.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/roles")
public class RoleController {
    private final UserClient userClient;

    @PutMapping("/assign")
    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR')")
    public List<UserDto> assignRoles(@RequestBody AssignRoleRequest assignRoleRequest) {
      return userClient.assignRoles(assignRoleRequest);
    }
}
