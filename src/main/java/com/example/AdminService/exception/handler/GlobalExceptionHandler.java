package com.example.AdminService.exception.handler;

import com.example.AdminService.dto.response.ApiResponse;
import com.example.AdminService.dto.response.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) throws JsonProcessingException {

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatMessage)
                .collect(Collectors.toList());



        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error(objectMapper.writeValueAsString(errors), HttpStatus.BAD_REQUEST.getReasonPhrase())
        );
    }

    private String formatMessage(FieldError error) {
        return String.format("Field '%s': %s", error.getField(), error.getDefaultMessage());
    }

}
