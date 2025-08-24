package com.example.AdminService.mapper;

import com.example.AdminService.dto.UserReadDto;
import com.example.AdminService.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.lang.annotation.Target;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    @Mapping(target = "id")
    UserReadDto toDto(User user);

    @Mapping(target = "id")
    User toEntity(UserReadDto userReadDto);
}
