package com.example.AdminService.service.impl;

import com.example.AdminService.dto.request.CourseCreateRequestDTO;
import com.example.AdminService.dto.request.CourseUpdateRequestDTO;
import com.example.AdminService.dto.response.AssignUsersResponse;
import com.example.AdminService.dto.response.CourseCreateResponseDTO;
import com.example.AdminService.dto.response.CourseResponseDTO;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.enums.CourseStatus;
import com.example.AdminService.utils.SecurityUtils;
import com.itechart.profileserviceapi.api.UserClient;
import com.itechart.profileserviceapi.dto.UserDto;
import com.itechart.profileserviceapi.dto.UserIdsRequest;
import com.itechart.profileserviceapi.enums.Role;
import com.example.AdminService.exception.CourseNotFoundException;
import com.example.AdminService.exception.CourseNameAlreadyTakenException;
import com.example.AdminService.mapper.CourseMapper;
import com.example.AdminService.repositories.CourseRepository;
import com.example.AdminService.service.CourseAuditService;
import com.example.AdminService.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;
    private final CourseAuditService audit;
    private final UserClient userClient;

    @Override
    public CourseCreateResponseDTO createCourse(CourseCreateRequestDTO courseDto) {
        if (courseRepository.existsCourseByName(courseDto.getName())) {
            throw new CourseNameAlreadyTakenException("Course with name '%s' already exists".formatted(courseDto.getName()));
        }


        UUID supervisorUUID = Objects.requireNonNull(SecurityUtils.getCurrentUser()).uuid();

        Course course = courseMapper.toEntity(courseDto);

        course.setStatus(CourseStatus.CREATED); // set to CREATED by default when course persisted for the first time
        course.setSupervisorId(supervisorUUID);
        Course savedCourse = courseRepository.save(course);

        audit.auditChange(
                savedCourse.getId(), "CREATED", "course", null, null, null
        );


        CourseCreateResponseDTO responseDTO = courseMapper.toDto(savedCourse);

        log.info("Course '{}' with description '{}' created", savedCourse.getName(), savedCourse.getDescription());
        return responseDTO;
    }

    @Override
    public CourseResponseDTO getById(Long id) {
        Optional<Course> courseById = courseRepository.findCourseByIdAndDeletedAtIsNull(id);
        return courseMapper.toResponseDto(courseById.orElseThrow(
                () -> new CourseNotFoundException("Course with provided id: '%s' not found".formatted(id))
        ));
    }

    @Override
    public List<CourseResponseDTO> findAllCourses(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Course> courses = courseRepository.findAllByDeletedAtIsNull(pageRequest);
        log.info("Total courses found: {}", courses.size());
        List<CourseResponseDTO> responseDTOS = new ArrayList<>();
        for (Course course : courses) {
            CourseResponseDTO responseDto = courseMapper.toResponseDto(course);
            responseDTOS.add(responseDto);
        }
        return responseDTOS;
    }

    @Override
    public List<CourseCreateResponseDTO> createCoursesBulk(List<CourseCreateRequestDTO> dtos) {
        List<CourseCreateResponseDTO> responseDTOS = new ArrayList<>();
        for (CourseCreateRequestDTO dto : dtos) {
            responseDTOS.add(createCourse(dto));
        }
        return responseDTOS;
    }

    @Override
    public CourseResponseDTO updateCourseById(Long id, CourseUpdateRequestDTO dto) {
        Course course = courseRepository.findCourseByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course with id '%s' not found. Cannot update".formatted(id)));

        boolean changed = false;

        if (dto.getName() != null && !dto.getName().trim().isEmpty()
                && !dto.getName().equals(course.getName())
                && !courseRepository.existsCourseByName(dto.getName())) {

            audit.auditChange(course.getId(), "UPDATED", "name",
                    course.getName(), dto.getName(), null);
            course.setName(dto.getName());
            changed = true;
        }

        if (dto.getDescription() != null && !dto.getDescription().trim().isEmpty()
                && !dto.getDescription().equals(course.getDescription())) {

            audit.auditChange(course.getId(), "UPDATED", "description",
                    course.getDescription(), dto.getDescription(), null);
            course.setDescription(dto.getDescription());
            changed = true;
        }

        if (dto.getCourseStatus() != null && !dto.getCourseStatus().equals(course.getStatus())) {
            audit.auditChange(course.getId(), "UPDATED", "status",
                    String.valueOf(course.getStatus()), String.valueOf(dto.getCourseStatus()), null);
            course.setStatus(dto.getCourseStatus());
            changed = true;
        }

        CourseResponseDTO responseDTO;
        if (changed) {
            course.setUpdatedAt(LocalDateTime.now());
            responseDTO = courseMapper.toResponseDto(courseRepository.save(course));
        } else {
            responseDTO = courseMapper.toResponseDto(course); // no update
        }

        return responseDTO;
    }

    @Override
    @Transactional
    public Map<String, String> deleteById(Long id) {
        var course = courseRepository.findCourseByIdAndDeletedAtIsNull(id).orElseThrow(
                () -> new CourseNotFoundException("Course with id '%s' not found. Cannot delete".formatted(id))
        );
        course.setDeletedAt(LocalDateTime.now());
        audit.auditChange(
                id,
                "UPDATE",
                "deleted_at",
                null,
                course.getDeletedAt().toString(),
                null

        );
        courseRepository.deleteMentorRelationsByCourse(id);
        courseRepository.deleteInternRelationsByCourse(id);
        courseRepository.save(course);

        return new HashMap<>(Map.of(
                "id", id.toString(),
                "deletedAt", course.getDeletedAt().toString()
        ));
    }

    @Override
    public AssignUsersResponse assignUsers(Long courseId, UserIdsRequest request) {
        Optional<Course> courseById = courseRepository.findById(courseId);

        if (courseById.isEmpty()) {
            throw new CourseNotFoundException(
                    "Course with id '%s' not found. Cannot assign users".formatted(courseId)
            );
        }

        Course course = courseById.get();

        ResponseEntity<List<UserDto>> response = userClient.findAllByIds(request);
        if (!response.getStatusCode().is2xxSuccessful()) {

        }
        List<UserDto> users = response.getBody();
        log.info("found users by ids: {}", users);
        assert users != null;
        var internIds = users.stream()
                .filter(userDto -> userDto.getRoles().contains(Role.ROLE_INTERN))
                .map(UserDto::getUuid).toList();

        var mentorIds = users.stream().filter(
                userDto -> userDto.getRoles().contains(Role.ROLE_MENTOR)).map(
                UserDto::getUuid).toList();

        course.getInternIds().addAll(internIds);
        course.getMentorIds().addAll(mentorIds);

        courseRepository.save(course);
        return new AssignUsersResponse(courseId, mentorIds, internIds);
    }

    @Override
    public Page<UserDto> findInternsByCourse(Long courseId, PageRequest pageRequest) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException("Course with id '%d' not found. Cannot fetch interns".formatted(courseId));
        }

        Page<UUID> internIdsPage = courseRepository.findInternIdsByCourseId(courseId, pageRequest);

        ResponseEntity<List<UserDto>> usersByIds = userClient.findAllByIds(new UserIdsRequest(internIdsPage.getContent()));

        if (usersByIds.getStatusCode().isError()) {
            throw new RuntimeException(
                    "Error while fetching user info from profile service. Status code: %s"
                            .formatted(usersByIds.getStatusCode())
            );
        }

        List<UserDto> users = Objects.requireNonNull(usersByIds.getBody())
                .stream()
                .sorted(Comparator.comparing(UserDto::getUsername))
                .toList();

        log.info("Total users found that are assigned to course '{}': {}", courseId, users.size());

        return new PageImpl<>(users, pageRequest, internIdsPage.getTotalElements());
    }


    @Override
    public List<UserDto> findMentorsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException("Course with id '%d' not found. Cannot fetch mentors");
        }

        List<UUID> mentorIds = courseRepository.findMentorsByCourseId(courseId);
        ResponseEntity<List<UserDto>> response = userClient.findAllByIds(new UserIdsRequest(
                mentorIds
        ));

        if (response.getStatusCode().isError()) {
            throw new RuntimeException("Error while fetching user info from profile service. Status code: %s".formatted(response.getStatusCode()));
        }

        return Objects.requireNonNull(response.getBody()).stream().sorted(Comparator.comparing(UserDto::getUsername)).toList();
    }

    @Override
    public UserIdsRequest unassignUsers(Long courseId, UserIdsRequest requestBody) {
        List<UUID> deletedIds = new LinkedList<>();
        deletedIds.addAll(courseRepository.unassignInterns(courseId, requestBody.userIds()));
        deletedIds.addAll(courseRepository.unassignMentors(courseId, requestBody.userIds()));
        return new UserIdsRequest(
                deletedIds
        );
    }
}
