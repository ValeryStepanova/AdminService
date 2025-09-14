package com.example.AdminService.dto.course;

import com.example.AdminService.dto.program.ProgramResponseMin;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseMentor;
import com.example.AdminService.enums.CourseStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {
    private Long id;
    private String name;
    private String description;
    private ProgramResponseMin program;
    private CourseStatus courseStatus;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;


    public CourseResponse(Course course, List<CourseMentor> mentors) {
        this.id = course.getId();
        this.name = course.getName();
        this.description = course.getDescription();
        this.program = new ProgramResponseMin(course.getProgram());
        this.courseStatus = course.getStatus();
        this.createdAt = course.getCreatedAt();
    }
}
