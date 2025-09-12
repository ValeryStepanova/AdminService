package com.example.AdminService.service.impl;

import com.example.AdminService.dto.UserPrincipal;
import com.example.AdminService.dto.program.ProgramRequest;
import com.example.AdminService.dto.program.ProgramResponse;
import com.example.AdminService.dto.program.ProgramResponseMin;
import com.example.AdminService.entities.Program;
import com.example.AdminService.entities.ProgramExpert;
import com.example.AdminService.enums.ProgramStatus;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.exception.ApiException;
import com.example.AdminService.repositories.ProgramExpertRepository;
import com.example.AdminService.repositories.ProgramRepository;
import com.example.AdminService.utils.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.AdminService.enums.ProgramStatus.DELETED;
import static com.example.AdminService.mapper.ProgramMapper.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProgramService {
    private final ProgramRepository programRepository;
    private final ProgramExpertRepository programExpertRepository;


    public List<ProgramResponseMin> getAll() {
        return toResponseList(programRepository.findAllByStatusNot(DELETED));
    }

    public ProgramResponse getById(Long id) {
        Program program = programRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        List<ProgramExpert> experts = programExpertRepository.findAllByProgramId(program.getId());

        return toResponse(program, experts);
    }

    public ProgramResponse create(ProgramRequest request) {
        if (programRepository.existsByNameAndStatusNot(request.name(), DELETED))
            throw new ApiException(ResponseStatus.PROGRAM_ALREADY_EXISTS);

        Program program = programRepository.save(toEntity(request));
        return toResponse(program, List.of());
    }

    public ProgramResponse updateById(Long id, ProgramRequest request) {
        Program program = programRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        if (programRepository.existsByNameAndStatusNot(request.name(), DELETED))
            throw new ApiException(ResponseStatus.PROGRAM_ALREADY_EXISTS);

        update(request, program);

        List<ProgramExpert> experts = programExpertRepository.findAllByProgramId(program.getId());
        return toResponse(programRepository.save(program), experts);
    }

    public ProgramResponse updateStatusById(Long id, ProgramStatus status) {
        if (status.equals(DELETED))
            throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

        Program program = programRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        program.setStatus(status);
        List<ProgramExpert> experts = programExpertRepository.findAllByProgramId(program.getId());

        return toResponse(programRepository.save(program), experts);
    }

    public void deleteById(Long id) {
        Program program = programRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        UserPrincipal currentUser = CurrentUserService.getCurrentUser();

        if (currentUser != null) {
            program.softDelete(currentUser.uuid());
            programRepository.save(program);
        } else {
            log.error("User details are not present in the context for supervisor in ProgramService.delete()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }
    }
}
