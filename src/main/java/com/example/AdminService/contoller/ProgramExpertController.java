package com.example.AdminService.contoller;

import com.example.AdminService.dto.HttpResponse;
import com.example.AdminService.dto.program.ProgramResponse;
import com.example.AdminService.service.impl.ProgramExpertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/program-expert")
@RequiredArgsConstructor
public class ProgramExpertController {
    private final ProgramExpertService programExpertService;


    @PostMapping("/assign/{program-id}/{expert-id}")
    public ResponseEntity<HttpResponse> assignExpert(
        @PathVariable("program-id") Long programId,
        @PathVariable("expert-id") UUID expertId
    ) {
        ProgramResponse program = programExpertService.assignExpert(programId, expertId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("program", program))
                .build()
        );
    }

    @PutMapping("/unassign/{program-id}/{expert-id}")
    public ResponseEntity<HttpResponse> unassignExpert(
        @PathVariable("program-id") Long programId,
        @PathVariable("expert-id") UUID expertId
    ) {
        ProgramResponse program = programExpertService.unassignExpert(programId, expertId);

        return ResponseEntity.ok(
            HttpResponse.builder()
                .statusCode(HttpStatus.OK.value())
                .description(HttpStatus.OK.name())
                .data(Map.of("program", program))
                .build()
        );
    }
}
