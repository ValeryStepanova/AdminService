package com.example.AdminService.mapper;

import com.example.AdminService.dto.response.UserResponse;
import com.example.AdminService.entities.Program;
import com.example.AdminService.entities.ProgramExpert;

public class ProgramExpertMapper {

    public static ProgramExpert toEntity(Program program, UserResponse expert) {
        ProgramExpert programExpert = new ProgramExpert();
        programExpert.setProgram(program);
        programExpert.setExpertId(expert.getId());
        programExpert.setUsername(expert.getUsername());
        programExpert.setExpertEmail(expert.getEmail());

        return programExpert;
    }

}
