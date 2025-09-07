package com.example.AdminService.contoller;

import com.example.AdminService.dto.request.CourseCreateRequestDTO;
import com.example.AdminService.dto.request.CourseUpdateRequestDTO;
import com.example.AdminService.dto.response.ApiResponse;
import com.example.AdminService.dto.response.AssignUsersResponse;
import com.example.AdminService.dto.response.CourseCreateResponseDTO;
import com.example.AdminService.dto.response.CourseResponseDTO;
import com.example.AdminService.service.CourseService;
import com.itechart.profileserviceapi.dto.UserIdsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@Slf4j
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<?>> createCoursesBulk(@RequestBody List<CourseCreateRequestDTO> dtos) {
        List<CourseCreateResponseDTO> response = courseService.createCoursesBulk(dtos);
        return ResponseEntity.status(
                HttpStatus.CREATED
        ).body(ApiResponse.success(response, "%d course saved in total".formatted(response.size())));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR')")
    public ResponseEntity<ApiResponse<?>> createCourse(@RequestBody @Valid CourseCreateRequestDTO courseDto) {
        CourseCreateResponseDTO savedCourse = courseService.createCourse(courseDto);
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(
                ApiResponse.success(savedCourse, "Course with provided details saved")
        );
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<ApiResponse<?>> getCourseById(@PathVariable Long id) {
        CourseResponseDTO course = courseService.getById(id);
        return ResponseEntity.status(HttpStatus.OK.value()).body(
                ApiResponse.success(course, "Course with provided id '%s' found".formatted(id))
        );
    }

    @PostMapping("/{courseId}/users")
    public ResponseEntity<ApiResponse<?>> assignUsers(
            @PathVariable Long courseId,
            @RequestBody UserIdsRequest request) {

        AssignUsersResponse response = courseService.assignUsers(courseId, request);

        return ResponseEntity.status(HttpStatus.CREATED.value()).body(
                ApiResponse.success(response, "users assigned successfully")
        );
    }


    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<CourseResponseDTO> foundCourses = courseService.findAllCourses(page, size);
        return ResponseEntity.status(HttpStatus.OK.value()).body(
                ApiResponse.success(foundCourses, "%d courses found in total".formatted(foundCourses.size()))
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR')")
    public ResponseEntity<ApiResponse<?>> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseUpdateRequestDTO requestDTO) {
        CourseResponseDTO responseDTO = courseService.updateCourseById(id, requestDTO);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(responseDTO, "Course updated successfully")
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_SUPERVISOR')")
    public ResponseEntity<ApiResponse<?>> deleteCourse(@PathVariable Long id) {
        var responseBody = courseService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.success(
                        responseBody,
                        "Course with id '%d' deleted successfully".formatted(id)
                )
        );
    }

}
