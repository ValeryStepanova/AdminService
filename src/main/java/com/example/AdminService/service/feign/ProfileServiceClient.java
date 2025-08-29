package com.example.AdminService.service.feign;
import org.example.dto.UserReadDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "user-profile", url = "http://user-profile:8080/users")
public interface ProfileServiceClient {

    @GetMapping
    Page<UserReadDto> findAll(@RequestParam("page") int page,
                              @RequestParam("size") int size);
    @GetMapping("/my_profile/{uuid}")
    UserReadDto getProfile(@PathVariable("uuid")UUID uuid);
}
