package com.example.AdminService.dto.task;

import com.example.AdminService.entities.TaskIntern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InternResponse {
    private UUID id;
    private String username;


    public InternResponse(TaskIntern intern) {
        this.id = intern.getInternId();
    }
}
