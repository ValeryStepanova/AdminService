package com.example.AdminService.mapper;

import com.example.AdminService.dto.ProgramDTO;
import com.example.AdminService.entities.Program;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ProgramMapper {
    ProgramMapper INSTANCE = Mappers.getMapper(ProgramMapper.class);

    @Mapping(target = "id")
    ProgramDTO toDto(Program program);

    @Mapping(target = "id")
    Program toEntity(ProgramDTO programDTO);

}
