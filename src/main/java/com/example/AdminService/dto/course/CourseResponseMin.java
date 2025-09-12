package com.example.AdminService.dto.course;

import com.example.AdminService.entities.Course;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseMin {
    private Long id;
    private String name;
    private String description;


    public CourseResponseMin(Course course) {
        this.id = course.getId();
        this.name = course.getName();
        this.description = course.getDescription();
    }
}
