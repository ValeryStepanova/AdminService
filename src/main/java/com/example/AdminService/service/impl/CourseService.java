package com.example.AdminService.service.impl;

import com.example.AdminService.dto.UserPrincipal;
import com.example.AdminService.dto.course.CourseRequest;
import com.example.AdminService.dto.course.CourseResponse;
import com.example.AdminService.dto.course.CourseResponseMin;
import com.example.AdminService.dto.course.CourseUpdateRequest;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseMentor;
import com.example.AdminService.entities.Program;
import com.example.AdminService.enums.ProgramStatus;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.enums.Role;
import com.example.AdminService.exception.ApiException;
import com.example.AdminService.repositories.CourseMentorRepository;
import com.example.AdminService.repositories.CourseRepository;
import com.example.AdminService.repositories.ProgramExpertRepository;
import com.example.AdminService.repositories.ProgramRepository;
import com.example.AdminService.utils.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static com.example.AdminService.enums.CourseStatus.DELETED;
import static com.example.AdminService.enums.SpecialistProgramStatus.ASSIGNED;
import static com.example.AdminService.mapper.CourseMapper.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;


@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final ProgramRepository programRepository;
    private final ProgramExpertRepository programExpertRepository;


    public List<CourseResponseMin> getAll() {
        return toResponseList(courseRepository.findAllByStatusNot(DELETED));
    }

    public List<CourseResponseMin> getAllByProgram(Long programId) {
        return toResponseList(courseRepository.findAllByProgram_IdAndStatusNot(programId, DELETED));
    }

    public CourseResponse getById(Long id) {
        Course course = courseRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());

        return toResponse(course, mentors);
    }

    public CourseResponse create(CourseRequest request) {
        Program program = programRepository.findByIdAndStatusNot(request.programId(), ProgramStatus.DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.PROGRAM_NOT_FOUND));

        if (courseRepository.existsByProgramAndNameAndStatusNot(program, request.name(), DELETED))
            throw new ApiException(ResponseStatus.COURSE_ALREADY_EXISTS);

        Course course;
        if (getCurrentUser().roles().contains(Role.SUPERVISOR.name()))
            course = courseRepository.save(toEntity(request, program, TRUE));
        else {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(program, getCurrentUserId(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            course = courseRepository.save(toEntity(request, program, FALSE));
            // TODO: send approval request to supervisor to create course
        }

        return toResponse(course, List.of());
    }

    public CourseResponse updateById(Long id, CourseUpdateRequest request) {
        Course course = courseRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        if (courseRepository.existsByProgramAndNameAndStatusNot(course.getProgram(), request.name(), DELETED))
            throw new ApiException(ResponseStatus.COURSE_ALREADY_EXISTS);

        if (getCurrentUser().roles().contains(Role.SUPERVISOR.name())) {
            update(request, course);
            courseRepository.saveAndFlush(course);
        } else {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), getCurrentUserId(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            // TODO: send approval request to supervisor to update course details
        }

        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
        return toResponse(course, mentors);
    }

    @Transactional
    public void deleteById(Long id) {
        Course course = courseRepository.findByIdAndStatusNot(id, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        UUID currentUserId = getCurrentUserId();
        if (getCurrentUser().roles().contains(Role.SUPERVISOR.name())) {
            course.softDelete(currentUserId);
            courseRepository.saveAndFlush(course);
        } else {
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUserId, ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            // TODO: send approval request to supervisor to delete course
        }

        // Delete all tasks associated with the course
        // taskService.deleteAllByCourseId(id);
    }

    private UserPrincipal getCurrentUser() {
        UserPrincipal currentUser = CurrentUserService.getCurrentUser();
        if (currentUser == null) {
            log.error("User details are not present in the context in CourseService.getCurrentUser()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        return currentUser;
    }

    private UUID getCurrentUserId() {
        return getCurrentUser().uuid();
    }
}
