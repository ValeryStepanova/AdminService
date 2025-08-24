package com.example.AdminService.dto.request;

import com.example.AdminService.entities.enums.CourseStatus;
import lombok.Data;

@Data
public class CourseUpdateRequestDTO {
    private String name;
    private String description;
    private CourseStatus courseStatus;
    private Long mentorId;
}
