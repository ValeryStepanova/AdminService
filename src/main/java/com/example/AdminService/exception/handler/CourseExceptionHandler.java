package com.example.AdminService.exception.handler;

import com.example.AdminService.dto.response.ApiResponse;
import com.example.AdminService.exception.CourseNameAlreadyTakenException;
import com.example.AdminService.exception.CourseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CourseExceptionHandler {
    @ExceptionHandler(CourseNameAlreadyTakenException.class)
    public ResponseEntity<ApiResponse<?>> handleCourseAlreadyExistsException(CourseNameAlreadyTakenException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(
                ApiResponse.error(e.getMessage(), HttpStatus.CONFLICT.getReasonPhrase())
        );
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCourseNotFoundException(CourseNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(
                ApiResponse.error(exception.getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase())
        );
    }
}
