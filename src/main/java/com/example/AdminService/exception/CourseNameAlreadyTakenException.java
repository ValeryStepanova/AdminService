package com.example.AdminService.exception;

public class CourseNameAlreadyTakenException extends RuntimeException {
    public CourseNameAlreadyTakenException(String message) {
        super(message);
    }
}
