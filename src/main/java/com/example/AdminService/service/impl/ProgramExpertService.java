package com.example.AdminService.service.impl;

import com.example.AdminService.client.AuthServiceClient;
import com.example.AdminService.dto.program.ProgramResponse;
import com.example.AdminService.dto.response.UserResponse;
import com.example.AdminService.entities.Program;
import com.example.AdminService.entities.ProgramExpert;
import com.example.AdminService.enums.ProgramStatus;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.enums.Role;
import com.example.AdminService.enums.SpecialistProgramStatus;
import com.example.AdminService.exception.ApiException;
import com.example.AdminService.repositories.ProgramExpertRepository;
import com.example.AdminService.repositories.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.example.AdminService.mapper.ProgramExpertMapper.toEntity;
import static com.example.AdminService.mapper.ProgramMapper.toResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramExpertService {
    private final ProgramExpertRepository programExpertRepository;
    private final ProgramRepository programRepository;
    private final AuthServiceClient authServiceClient;


    public ProgramResponse assignExpert(Long programId, UUID expertId) {
        Program program = programRepository.findByIdAndStatusNot(programId, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        UserResponse expert = authServiceClient.getUserById(expertId);
        if (!expert.getRole().equals(Role.EXPERT))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        Optional<ProgramExpert> optionalProgramExpert = programExpertRepository.findByProgram_IdAndExpertId(programId, expertId);
        if (optionalProgramExpert.isEmpty()) {
            programExpertRepository.saveAndFlush(toEntity(program, expert));
        } else {
            ProgramExpert programExpert = optionalProgramExpert.get();
            if (programExpert.getStatus().equals(SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_ASSIGNED);

            programExpert.setStatus(SpecialistProgramStatus.ASSIGNED);
            programExpertRepository.saveAndFlush(programExpert);
        }

        return toResponse(program, programExpertRepository.findAllByProgramId(program.getId()));
    }

    public ProgramResponse unassignExpert(Long programId, UUID expertId) {
        Program program = programRepository.findByIdAndStatusNot(programId, ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        ProgramExpert programExpert = programExpertRepository
            .findByProgram_IdAndExpertId(program.getId(), expertId)
            .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

        if (programExpert.getStatus().equals(SpecialistProgramStatus.UNASSIGNED))
            throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

        programExpert.setStatus(SpecialistProgramStatus.UNASSIGNED);
        programExpertRepository.saveAndFlush(programExpert);

        return toResponse(program, programExpertRepository.findAllByProgramId(program.getId()));
    }
}
