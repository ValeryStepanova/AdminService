package com.example.AdminService.contoller;

import com.example.AdminService.dto.HttpResponse;
import com.example.AdminService.dto.course.CourseRequest;
import com.example.AdminService.dto.course.CourseResponse;
import com.example.AdminService.dto.course.CourseResponseMin;
import com.example.AdminService.dto.course.CourseUpdateRequest;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.service.impl.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;


    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAll() {
        List<CourseResponseMin> courses = courseService.getAll();

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("course", courses))
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> get(@PathVariable Long id) {
        CourseResponse course = courseService.getById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("course", course))
                .build()
        );
    }

    @PostMapping()
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
}
