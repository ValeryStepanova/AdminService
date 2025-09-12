package com.example.AdminService.contoller;

import com.example.AdminService.dto.HttpResponse;
import com.example.AdminService.dto.program.ProgramRequest;
import com.example.AdminService.dto.program.ProgramResponse;
import com.example.AdminService.dto.program.ProgramResponseMin;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.service.impl.ProgramService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/program")
@RequiredArgsConstructor
public class ProgramController {
    private final ProgramService programService;


    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAll() {
        List<ProgramResponseMin> programs = programService.getAll();

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("program", programs))
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getById(@PathVariable Long id) {
        ProgramResponse program = programService.getById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("program", program))
                .build()
        );
    }

    @PostMapping()
    public ResponseEntity<HttpResponse> create(@RequestBody @Valid ProgramRequest request) {
        ProgramResponse program = programService.create(request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data(Map.of("program", program))
                .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> update(@PathVariable Long id, @RequestBody @Valid ProgramRequest request) {
        ProgramResponse program = programService.updateById(id, request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("program", program))
                .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> delete(@PathVariable Long id) {
        programService.deleteById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data("Program deleted successfully")
                .build()
        );
    }
}
