package com.example.AdminService.service.impl;

import com.example.AdminService.service.feign.ProfileServiceClient;
import lombok.RequiredArgsConstructor;
import org.example.dto.UserReadDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FeignService {
    private final ProfileServiceClient profileServiceClient;

    public Page<UserReadDto> fetchAllUsers(int page, int size) {
        return profileServiceClient.findAll(page, size);
    }
    public UserReadDto getByUUID(UUID uuid){
        return profileServiceClient.getProfile(uuid);
    }
}
