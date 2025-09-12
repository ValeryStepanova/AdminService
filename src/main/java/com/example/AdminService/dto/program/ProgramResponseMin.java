package com.example.AdminService.dto.program;

import com.example.AdminService.entities.Program;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProgramResponseMin {
    private Long id;
    private String name;
    private String description;


    public ProgramResponseMin(Program program) {
        this.id = program.getId();
        this.name = program.getName();
        this.description = program.getDescription();
    }
}
