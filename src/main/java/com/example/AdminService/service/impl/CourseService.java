package com.example.AdminService.service.impl;

import com.example.AdminService.dto.UserPrincipal;
import com.example.AdminService.dto.course.*;
import com.example.AdminService.dto.response.AssignUsersResponse;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseIntern;
import com.example.AdminService.entities.CourseMentor;
import com.example.AdminService.entities.Program;
import com.example.AdminService.enums.CourseStatus;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.enums.Role;
import com.example.AdminService.enums.SpecialistProgramStatus;
import com.example.AdminService.exception.ApiException;
import com.example.AdminService.exception.CourseNotFoundException;
import com.example.AdminService.mapper.CourseMapper;
import com.example.AdminService.repositories.CourseInternRepository;
import com.example.AdminService.repositories.CourseMentorRepository;
import com.example.AdminService.repositories.CourseRepository;
import com.example.AdminService.repositories.ProgramExpertRepository;
import com.example.AdminService.utils.CurrentUserService;
import com.itechart.profileserviceapi.api.UserClient;
import com.itechart.profileserviceapi.dto.UserDto;
import com.itechart.profileserviceapi.dto.UserIdsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.example.AdminService.enums.CourseStatus.DELETED;
import static com.example.AdminService.enums.SpecialistProgramStatus.ASSIGNED;
import static com.example.AdminService.mapper.CourseMapper.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseInternRepository courseInternRepository;
    private final CourseRepository courseRepository;
    private final ProgramService programService;
    private final ProgramExpertRepository programExpertRepository;
    private final CourseMapper courseMapper;
    private final UserClient userClient;
    private final CourseMentorRepository courseMentorRepository;

    public List<CourseResponse> createCoursesBulk(List<CourseRequest> dtos) {
        List<CourseResponse> responseDTOS = new ArrayList<>();
        for (var dto : dtos) {
            responseDTOS.add(create(dto));
        }
        return responseDTOS;
    }

    public Page<CourseResponse> findAllCourses(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Course> courses = courseRepository.findAllByStatusNot(DELETED, pageRequest);
        return courses.map(courseMapper::toResponse);
    }

    public List<CourseResponseMin> getAllByProgram(Long programId) {
        return toResponseList(courseRepository.findAllByProgram_IdAndStatusNot(programId, DELETED));
    }

    public CourseResponse getById(Long id) {
        Course course = courseRepository.findByIdAndStatusNot(id, DELETED)
                .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));
        return courseMapper.toResponse(course);
    }

    public CourseResponse create(CourseRequest request) {
        if (!programService.existsById(request.programId())) {
            throw new ApiException(ResponseStatus.PROGRAM_NOT_FOUND);
        }

        if (courseRepository.existsByProgram_IdAndNameAndStatusNot(request.programId(), request.name(), DELETED)) {
            throw new ApiException(ResponseStatus.COURSE_ALREADY_EXISTS);
        }

        Course course = courseMapper.toEntity(request);
        CourseStatus status;
        if (getCurrentUser().roles().contains(Role.SUPERVISOR.name())) {
            status = CourseStatus.ACTIVE;
        } else {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgram_IdAndExpertIdAndStatus(request.programId(), getCurrentUserId(), ASSIGNED)) {
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
            }

            status = CourseStatus.PENDING_APPROVAL;
            // TODO: send approval request to supervisor to create course
        }
        Program program = programService.findById(request.programId());
        course.setProgram(program);
        course.setStatus(status);
        Course savedCourse = courseRepository.save(course);

        return courseMapper.toResponse(savedCourse);
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
        return courseMapper.toResponse(course);
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

    public AssignUsersResponse assignUsers(Long courseId, UserIdsRequest request) {
        Optional<Course> courseById = courseRepository.findById(courseId);

        if (courseById.isEmpty()) {
            throw new CourseNotFoundException(
                    "Course with id '%s' not found. Cannot assign users".formatted(courseId)
            );
        }

        Course course = courseById.get();

        ResponseEntity<List<UserDto>> response = userClient.findAllByIds(request);


        List<UserDto> users = response.getBody();
        log.info("found users by ids: {}", users);
        assert users != null;

        var internIds = users.stream()
                .filter(userDto -> userDto.getRoles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_INTERN))
                .map(UserDto::getUuid).toList();

        var mentorIds = users.stream().filter(
                userDto -> userDto.getRoles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_MENTOR)).map(
                UserDto::getUuid).toList();

        List<CourseIntern> courseInterns = internIds.stream().map(
                internId -> CourseIntern.builder()
                        .internId(internId)
                        .course(course)
                        .status(ASSIGNED)
                        .build()
        ).toList();

        courseInternRepository.saveAll(courseInterns);

        List<CourseMentor> courseMentors = mentorIds.stream().map(mentorId -> CourseMentor.builder()
                .mentorId(mentorId)
                .status(ASSIGNED)
                .course(course)
                .mentorId(mentorId)
                .build()).toList();

        courseMentorRepository.saveAll(courseMentors);

        return new AssignUsersResponse(courseId, course.getName(), mentorIds, internIds);
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

    public Page<UserDto> findInternsByCourse(Long courseId, PageRequest pageRequest) {
        if (!courseRepository.existsById(courseId)) {
            throw new ApiException(
                    ResponseStatus.COURSE_NOT_FOUND
            );
        }
        Page<UUID> internIds = courseInternRepository.findCourseInternByCourseId(courseId, pageRequest);


        if (internIds.isEmpty()) {
            // returns the empty list without calling external api
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        }

        ResponseEntity<List<UserDto>> usersByIds = userClient.findAllByIds(new UserIdsRequest(internIds.getContent()));

        if (usersByIds.getStatusCode().isError()) {
            throw new RuntimeException(
                    "Error while fetching user info from profile service. Status code: %s"
                            .formatted(usersByIds.getStatusCode())
            );
        }
        List<UserDto> foundUsers = usersByIds.getBody();


        List<UserDto> users = Objects.requireNonNull(foundUsers)
                .stream()
                .sorted(Comparator.comparing(UserDto::getUsername))
                .toList();

        log.info("Total users found that are assigned to course '{}': {}", courseId, users.size());

        return new PageImpl<>(users, pageRequest, internIds.getTotalElements());
    }


    public List<UserDto> getMentorsByCourse(Long courseId) {
        if (!courseRepository.existsCourseByIdAndStatusNot(courseId, DELETED)) {
            throw new ApiException(
                    ResponseStatus.COURSE_NOT_FOUND
            );
        }

        List<UUID> courseMentors = courseMentorRepository.findCourseMentorsByCourseId(courseId);

        if (courseMentors.isEmpty()) {
            // Returns the empty list if no mentors assigned to course
            return Collections.emptyList();
        }

        ResponseEntity<List<UserDto>> response = userClient.findAllByIds(new UserIdsRequest(courseMentors));
        return response.getBody();
    }

    public UnassignUsersResponse unassignUsers(Long courseId, UserIdsRequest requestBody) {
        List<UUID> userIds = requestBody.userIds();
        List<UUID> unassignedInternIds = courseInternRepository.unassign(courseId, userIds);
        List<UUID> unassignedMentorIds = courseMentorRepository.unassign(courseId, userIds);
        return new UnassignUsersResponse(
                courseId,
                unassignedInternIds,
                unassignedMentorIds
        );
    }
}
