package com.example.AdminService.contoller;

import com.example.AdminService.dto.HttpResponse;
import com.example.AdminService.dto.task.TaskRequest;
import com.example.AdminService.dto.task.TaskResponse;
import com.example.AdminService.dto.task.TaskResponseMin;
import com.example.AdminService.dto.task.TaskUpdateRequest;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.service.impl.TaskService;
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
@RequestMapping("/api/v1/admin/task")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;


    @GetMapping("/all")
    public ResponseEntity<HttpResponse> getAll( @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {

        Page<TaskResponseMin> tasks = taskService.getAll(page,size);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(com.example.AdminService.enums.ResponseStatus.OK.getStatusCode())
                .description(com.example.AdminService.enums.ResponseStatus.OK.getDescription())
                .data(Map.of("tasks", tasks))
                .build()
        );
    }

    @GetMapping("/all/by-program/{program-id}")
    public ResponseEntity<HttpResponse> getAllByProgram(@PathVariable("program-id") Long programId) {
        List<TaskResponseMin> tasks = taskService.getAllByCourseId(programId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(com.example.AdminService.enums.ResponseStatus.OK.getStatusCode())
                .description(com.example.AdminService.enums.ResponseStatus.OK.getDescription())
                .data(Map.of("tasks", tasks))
                .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> get(@PathVariable Long id) {
        TaskResponse task = taskService.getById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(com.example.AdminService.enums.ResponseStatus.OK.getStatusCode())
                .description(ResponseStatus.OK.getDescription())
                .data(Map.of("task", task))
                .build()
        );
    }

    @PostMapping()
    public ResponseEntity<HttpResponse> create(@RequestBody @Valid TaskRequest request) {
        TaskResponse taskResponse = taskService.create(request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.CREATED.value())
                .description(HttpStatus.CREATED.name())
                .data(Map.of("taskResponse", taskResponse))
                .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<HttpResponse> update(@PathVariable Long id, @RequestBody @Valid TaskUpdateRequest request) {
        TaskResponse taskResponse = taskService.updateById(id, request);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("taskResponse", taskResponse))
                .build()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse> delete(@PathVariable Long id) {
        taskService.deleteById(id);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data("Task deleted successfully")
                .build()
        );
    }
}
