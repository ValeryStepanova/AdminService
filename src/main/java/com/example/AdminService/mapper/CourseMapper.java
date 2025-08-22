package com.example.AdminService.mapper;

import com.example.AdminService.dto.CourseDTO;
import com.example.AdminService.entities.Course;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseMapper {
    CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

    @Mapping(target = "id")
    CourseDTO toDto(Course course);
    @Mapping(target = "id")
    Course toEntity(CourseDTO courseDTO);

}
