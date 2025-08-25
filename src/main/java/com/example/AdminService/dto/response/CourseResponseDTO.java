package com.example.AdminService.dto.response;

import com.example.AdminService.entities.enums.CourseStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDTO {
    private Long id;
    private String name;
    private String description;
    private CourseStatus status;
    private String mentorUsername;
}
