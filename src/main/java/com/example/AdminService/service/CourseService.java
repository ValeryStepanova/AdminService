package com.example.AdminService.service;

import com.example.AdminService.dto.request.CourseCreateRequestDTO;
import com.example.AdminService.dto.request.CourseUpdateRequestDTO;
import com.example.AdminService.dto.response.AssignUsersResponse;
import com.example.AdminService.dto.response.CourseCreateResponseDTO;
import com.example.AdminService.dto.response.CourseResponseDTO;
import com.itechart.profileserviceapi.dto.UserIdsRequest;

import java.util.List;
import java.util.Map;

public interface CourseService {
    CourseCreateResponseDTO createCourse(CourseCreateRequestDTO courseDto);

    CourseResponseDTO getById(Long id);

    List<CourseResponseDTO> findAllCourses(int page, int size);

    List<CourseCreateResponseDTO> createCoursesBulk(List<CourseCreateRequestDTO> dtos);

    CourseResponseDTO updateCourseById(Long id, CourseUpdateRequestDTO requestDTO);

    Map<String, String> deleteById(Long id);

    AssignUsersResponse assignUsers(Long courseId, UserIdsRequest request);
}
