package com.example.AdminService.dto;

import com.example.AdminService.entities.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Value;

import java.util.UUID;

@Builder(toBuilder = true)
@Data
@Value
public class UserReadDto {
    @JsonIgnore
    Long id;
    @JsonIgnore
    UUID uuid;
    String username;
    String phoneNumber;
    String city;
    String email;
    Role role;

}
