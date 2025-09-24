package com.example.AdminService.contoller;

import com.example.AdminService.dto.HttpResponse;
import com.example.AdminService.dto.response.AssignInternsResponse;
import com.example.AdminService.dto.task.TaskResponse;
import com.example.AdminService.service.impl.TaskInternService;
import com.itechart.admin_service_api.dto.TaskInternDto;
import com.itechart.profileserviceapi.dto.UserIdsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/task-intern")
@RequiredArgsConstructor
public class TaskInternController {
    private final TaskInternService taskInternService;


    @PostMapping("/assign/{task-id}")
    public ResponseEntity<HttpResponse> assignIntern(
        @PathVariable("task-id") Long taskId,
       @RequestBody UserIdsRequest userIdsRequest
    ) {
        AssignInternsResponse task = taskInternService.assignIntern(taskId, userIdsRequest);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("task", task))
                .build()
        );
    }

    @PutMapping("/unassign/{task-id}/{intern-id}")
    public ResponseEntity<HttpResponse> unassignIntern(
        @PathVariable("task-id") Long taskId,
        @PathVariable("intern-id") UUID internId
    ) {
        TaskResponse task = taskInternService.unassignIntern(taskId, internId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("task", task))
                .build()
        );
    }

    @GetMapping("/{taskInternId}")
    ResponseEntity<TaskInternDto> getTaskInternById(@PathVariable Long taskInternId){
        return ResponseEntity.ok(taskInternService.getTaskInternById(taskInternId));

    }
}
