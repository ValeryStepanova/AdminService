package com.example.AdminService.contoller;

import com.example.AdminService.dto.HttpResponse;
import com.example.AdminService.dto.course.CourseRequest;
import com.example.AdminService.dto.course.CourseResponse;
import com.example.AdminService.dto.course.CourseUpdateRequest;
import com.example.AdminService.dto.course.UnassignUsersResponse;
import com.example.AdminService.dto.response.AssignUsersResponse;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.service.impl.CourseService;
import com.itechart.profileserviceapi.dto.UserDto;
import com.itechart.profileserviceapi.dto.UserIdsRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/bulk")
    public ResponseEntity<HttpResponse> createCoursesBulk(@RequestBody List<CourseRequest> dtos) {
        List<CourseResponse> response = courseService.createCoursesBulk(dtos);
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .responseStatus(ResponseStatus.CREATED)
                        .description("%d courses created successfully in total".formatted(response.size()))
                        .data(response).build()

        );
    }

    @GetMapping
    public ResponseEntity<HttpResponse> getAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<CourseResponse> foundCourses = courseService.findAllCourses(page, size);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .responseStatus(ResponseStatus.OK)
                        .description(ResponseStatus.OK.getDescription())
                        .data(foundCourses)
                        .build()
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> get(@PathVariable Long id) {
        CourseResponse course = courseService.getById(id);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .responseStatus(ResponseStatus.OK)
                        .description(ResponseStatus.OK.getDescription())
                        .data(Map.of("course", course))
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<HttpResponse> create(@RequestBody @Valid CourseRequest request) {
        CourseResponse course = courseService.create(request);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .description(HttpStatus.CREATED.name())
                        .data(Map.of("course", course))
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> update(@PathVariable Long id, @RequestBody @Valid CourseUpdateRequest request) {
        CourseResponse course = courseService.updateById(id, request);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .description(HttpStatus.OK.name())
                        .data(Map.of("course", course))
                        .build()
        );
    }


    @PostMapping("/{course-id}/users")
    public ResponseEntity<HttpResponse> assignUsers(
            @PathVariable(name = "course-id") Long courseId,
            @RequestBody UserIdsRequest request) {

        AssignUsersResponse response = courseService.assignUsers(courseId, request);

        return ResponseEntity.ok(
                new HttpResponse(
                        ResponseStatus.CREATED.getHttpStatus().value(),
                        ResponseStatus.CREATED.getDescription(),
                        response,
                        ResponseStatus.CREATED
                )
        );
    }

    @GetMapping("/{course-id}/interns")
    public ResponseEntity<HttpResponse> getCourseInterns(
            @PathVariable(name = "course-id") Long courseId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<UserDto> internsByCourse = courseService.findInternsByCourse(courseId, pageRequest);
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .responseStatus(ResponseStatus.OK)
                        .description(ResponseStatus.OK.getDescription())
                        .data(internsByCourse)
                        .build()
        );
    }

    @GetMapping("/{course-id}/mentors")
    public ResponseEntity<HttpResponse> getCourseMentors(
            @PathVariable(name = "course-id") Long courseId
    ) {
        List<UserDto> mentorsByCourse = courseService.getMentorsByCourse(courseId);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .data(mentorsByCourse)
                        .description("All mentors for course with id '%d'".formatted(courseId))
                        .statusCode(ResponseStatus.OK.getHttpStatus().value())
                        .responseStatus(ResponseStatus.OK)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> delete(@PathVariable Long id) {
        courseService.deleteById(id);

        return ResponseEntity.ok(
                HttpResponse.builder()
                        .statusCode(HttpStatus.OK.value())
                        .description(HttpStatus.OK.name())
                        .data("Course deleted successfully")
                        .build()
        );
    }

    @DeleteMapping("/{courseId}/users")
    public ResponseEntity<HttpResponse> unassignUsers(
            @PathVariable(name = "courseId") Long courseId,
            @RequestBody UserIdsRequest requestBody
    ) {
        UnassignUsersResponse responseBody = courseService.unassignUsers(courseId, requestBody);
        return ResponseEntity.ok(
                new HttpResponse(
                        ResponseStatus.OK.getHttpStatus().value(),
                        "User unassigned succesfully",
                        responseBody,
                        ResponseStatus.OK
                )
        );
    }
}
