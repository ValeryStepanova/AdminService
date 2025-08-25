package com.example.AdminService.mapper;

import com.example.AdminService.dto.request.CourseCreateRequestDTO;
import com.example.AdminService.dto.response.CourseCreateResponseDTO;
import com.example.AdminService.dto.response.CourseResponseDTO;
import com.example.AdminService.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {
    CourseCreateResponseDTO toDto(Course course);
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    Course toEntity(CourseCreateRequestDTO courseCreateRequestDTO);

    CourseResponseDTO toResponseDto(Course course);
}
