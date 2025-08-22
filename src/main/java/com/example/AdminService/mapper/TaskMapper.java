package com.example.AdminService.mapper;

import com.example.AdminService.dto.TaskDTO;
import com.example.AdminService.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    @Mapping(target = "id")
    TaskDTO toDto(Task task);

    @Mapping(target = "id")
    Task toEntity(TaskDTO taskDTO);
}
