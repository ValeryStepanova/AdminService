package com.example.AdminService.dto.request;

import lombok.Data;

import com.example.AdminService.enums.CourseStatus;

@Data
public class CourseUpdateRequestDTO {
    private String name;
    private String description;
    private CourseStatus courseStatus;
    private Long mentorId;
}
