package com.example.AdminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.AdminService.enums.CourseStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDTO {
    private Long id;
    private String name;
    private String description;
    private CourseStatus status;
}
