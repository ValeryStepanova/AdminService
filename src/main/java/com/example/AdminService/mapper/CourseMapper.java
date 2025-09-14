package com.example.AdminService.mapper;

import com.example.AdminService.dto.course.CourseRequest;
import com.example.AdminService.dto.course.CourseResponse;
import com.example.AdminService.dto.course.CourseResponseMin;
import com.example.AdminService.dto.course.CourseUpdateRequest;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseMentor;
import com.example.AdminService.entities.Program;
import com.example.AdminService.enums.CourseStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CourseMapper {

    public static List<CourseResponseMin> toResponseList(List<Course> courses) {
        return courses.stream().map(CourseResponseMin::new).toList();
    }

    public static CourseResponse toResponse(Course course, List<CourseMentor> mentors) {
        return new CourseResponse(course, mentors);
    }

    static void update(CourseUpdateRequest request, Course course) {
        BeanUtils.copyProperties(request, course);
    }

    @Mapping(source = "program.id", target = "program.id")
    @Mapping(source = "program.description", target = "program.description")
    @Mapping(source = "program.name", target = "program.name")
    @Mapping(source = "createdAt", target = "createdAt")
    CourseResponse toResponse(Course course);

    Course toEntity(CourseRequest request);
}
