package com.example.AdminService.dto;

import com.itechart.profileserviceapi.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import lombok.Value;

import java.util.List;
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
    List<Role> roles;

}
