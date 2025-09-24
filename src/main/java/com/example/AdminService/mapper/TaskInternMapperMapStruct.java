package com.example.AdminService.mapper;

import com.example.AdminService.entities.TaskIntern;
import com.itechart.admin_service_api.dto.TaskInternDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskInternMapperMapStruct {
    TaskInternMapperMapStruct INSTANCE = Mappers.getMapper(TaskInternMapperMapStruct.class);
    @Mapping(target = "id")
    TaskInternDto toDto(TaskIntern taskIntern);

}
