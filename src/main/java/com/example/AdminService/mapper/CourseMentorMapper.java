package com.example.AdminService.mapper;

import com.example.AdminService.dto.UserPrincipal;
import com.example.AdminService.dto.response.UserResponse;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseMentor;

public class CourseMentorMapper {

    public static CourseMentor toEntity(Course course, UserResponse mentor) {
        CourseMentor courseMentor = new CourseMentor();
        courseMentor.setCourse(course);
        courseMentor.setMentorId(mentor.getId());
        return courseMentor;
    }

    public static CourseMentor toEntity(Course course, UserPrincipal mentor) {
        CourseMentor courseMentor = new CourseMentor();
        courseMentor.setCourse(course);
        courseMentor.setMentorId(mentor.uuid());
        return courseMentor;
    }

}
