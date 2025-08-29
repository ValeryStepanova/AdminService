package com.example.AdminService.contoller;

import com.example.AdminService.service.impl.FeignService;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserReadDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class UserProfileController {

    private final FeignService feignService;

    @GetMapping("/getProfile/{uuid}")
    public ResponseEntity<UserReadDto> getProfile(@PathVariable UUID uuid){
        return ResponseEntity.ok(feignService.getByUUID(uuid));
    }
}
