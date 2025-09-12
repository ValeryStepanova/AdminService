package com.example.AdminService.mapper;

import com.example.AdminService.dto.course.CourseRequest;
import com.example.AdminService.dto.course.CourseResponse;
import com.example.AdminService.dto.course.CourseResponseMin;
import com.example.AdminService.dto.course.CourseUpdateRequest;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseMentor;
import com.example.AdminService.entities.Program;
import com.example.AdminService.enums.CourseStatus;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class CourseMapper {

    public static List<CourseResponseMin> toResponseList(List<Course> courses) {
        return courses.stream().map(CourseResponseMin::new).toList();
    }

    public static Course toEntity(CourseRequest request, Program program, Boolean isSupervisor) {
        Course course = new Course();
        BeanUtils.copyProperties(request, course, "programId");
        course.setProgram(program);
        if (isSupervisor)
            course.setStatus(CourseStatus.ACTIVE);      // Course is being created by Supervisor. Thus, ACTIVE by default.
        else
            course.setStatus(CourseStatus.PENDING_APPROVAL);

        return course;
    }

    public static CourseResponse toResponse(Course course, List<CourseMentor> mentors) {
        return new CourseResponse(course, mentors);
    }

    public static void update(CourseUpdateRequest request, Course course) {
        BeanUtils.copyProperties(request, course);
    }

}
