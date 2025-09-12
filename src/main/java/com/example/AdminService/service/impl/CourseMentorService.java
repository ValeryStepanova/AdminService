package com.example.AdminService.service.impl;

import com.example.AdminService.client.AuthServiceClient;
import com.example.AdminService.dto.UserPrincipal;
import com.example.AdminService.dto.course.CourseResponse;
import com.example.AdminService.dto.response.UserResponse;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseMentor;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.enums.Role;
import com.example.AdminService.exception.ApiException;
import com.example.AdminService.repositories.CourseMentorRepository;
import com.example.AdminService.repositories.CourseRepository;
import com.example.AdminService.repositories.ProgramExpertRepository;
import com.example.AdminService.utils.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.example.AdminService.enums.CourseStatus.DELETED;
import static com.example.AdminService.enums.SpecialistProgramStatus.ASSIGNED;
import static com.example.AdminService.enums.SpecialistProgramStatus.UNASSIGNED;
import static com.example.AdminService.mapper.CourseMapper.toResponse;
import static com.example.AdminService.mapper.CourseMentorMapper.toEntity;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourseMentorService {
    private final CourseRepository courseRepository;
    private final AuthServiceClient authServiceClient;
    private final CourseMentorRepository courseMentorRepository;
    private final ProgramExpertRepository programExpertRepository;


    public CourseResponse assignMentor(Long courseId, UUID mentorId) {
        Course course = courseRepository.findByIdAndStatusNot(courseId, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        UserResponse mentor = authServiceClient.getUserById(mentorId);
        if (!mentor.getRole().equals(Role.MENTOR))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        if (getCurrentUser().roles().contains(Role.SUPERVISOR.name())) {
            Optional<CourseMentor> optionalCourseMentor = courseMentorRepository.findByCourse_IdAndMentorId(courseId, mentorId);
            if (optionalCourseMentor.isEmpty()) {
                courseMentorRepository.saveAndFlush(toEntity(course, mentor));
            } else {
                CourseMentor courseMentor = optionalCourseMentor.get();
                if (courseMentor.getStatus().equals(ASSIGNED))
                    throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_ASSIGNED);

                courseMentor.setStatus(ASSIGNED);
                courseMentorRepository.saveAndFlush(courseMentor);
            }
        }
        else {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), getExpertId(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            Optional<CourseMentor> optionalCourseMentor = courseMentorRepository.findByCourse_IdAndMentorId(courseId, mentorId);
            if (optionalCourseMentor.isEmpty()) {
                log.info("Course Mentor not found, creating new one");
                // TODO: send approval request to supervisor to assign the mentor
            } else {
                CourseMentor courseMentor = optionalCourseMentor.get();
                if (courseMentor.getStatus().equals(ASSIGNED))
                    throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_ASSIGNED);

                // TODO: send approval request to supervisor to assign the mentor
            }

        }

        return toResponse(course, courseMentorRepository.findAllByCourseId(course.getId()));
    }

    public CourseResponse unassignMentor(Long courseId, UUID mentorId) {
        Course course = courseRepository.findByIdAndStatusNot(courseId, DELETED)
            .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        if (getCurrentUser().roles().contains(Role.SUPERVISOR.name())) {
            CourseMentor courseMentor = courseMentorRepository
                .findByCourse_IdAndMentorId(course.getId(), mentorId)
                .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (courseMentor.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            courseMentor.setStatus(UNASSIGNED);
            courseMentorRepository.saveAndFlush(courseMentor);
        } else {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), getExpertId(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            CourseMentor courseMentor = courseMentorRepository
                .findByCourse_IdAndMentorId(course.getId(), mentorId)
                .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (courseMentor.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            // TODO: send approval request to supervisor to unassign the mentor
        }

        return toResponse(course, courseMentorRepository.findAllByCourseId(course.getId()));
    }

    private UserPrincipal getCurrentUser() {
        UserPrincipal currentUser = CurrentUserService.getCurrentUser();
        if (currentUser == null) {
            log.error("User details are not present in the context in CourseService.getCurrentUser()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        return currentUser;
    }

    private UUID getExpertId() {
        return getCurrentUser().uuid();
    }
}
