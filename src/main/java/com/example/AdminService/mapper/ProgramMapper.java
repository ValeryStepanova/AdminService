package com.example.AdminService.mapper;

import com.example.AdminService.dto.program.ProgramRequest;
import com.example.AdminService.dto.program.ProgramResponse;
import com.example.AdminService.dto.program.ProgramResponseMin;
import com.example.AdminService.entities.Program;
import com.example.AdminService.entities.ProgramExpert;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class ProgramMapper {

    public static Program toEntity(ProgramRequest request) {
        Program program = new Program();
        BeanUtils.copyProperties(request, program);

        return program;
    }

    public static ProgramResponse toResponse(Program program, List<ProgramExpert> experts) {
        return new ProgramResponse(program, experts);
    }

    public static List<ProgramResponseMin> toResponseList(List<Program> programs) {
        return programs.stream().map(ProgramResponseMin::new).toList();
    }

    public static void update(ProgramRequest request, Program program) {
        BeanUtils.copyProperties(request, program);
    }

}
