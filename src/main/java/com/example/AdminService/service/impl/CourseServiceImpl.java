package com.example.AdminService.service.impl;

import com.example.AdminService.dto.request.CourseCreateRequestDTO;
import com.example.AdminService.dto.request.CourseUpdateRequestDTO;
import com.example.AdminService.dto.response.CourseCreateResponseDTO;
import com.example.AdminService.dto.response.CourseResponseDTO;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.User;
import com.example.AdminService.entities.enums.CourseStatus;
import com.itechart.profileserviceapi.enums.Role;
import com.example.AdminService.exception.CourseNotFoundException;
import com.example.AdminService.exception.CourseNameAlreadyTakenException;
import com.example.AdminService.exception.UserNotFoundException;
import com.example.AdminService.mapper.CourseMapper;
import com.example.AdminService.repositories.CourseRepository;
import com.example.AdminService.service.CourseAuditService;
import com.example.AdminService.service.CourseService;
import com.example.AdminService.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final UserService userService;
    private final CourseMapper courseMapper;
    private final CourseAuditService audit;

    @Override
    public CourseCreateResponseDTO createCourse(CourseCreateRequestDTO courseDto) {
        if (courseRepository.existsCourseByName(courseDto.getName())) {
            throw new CourseNameAlreadyTakenException("Course with name '%s' already exists".formatted(courseDto.getName()));
        }
        if (!userService.existsByIdAndRole(courseDto.getMentorId(), Role.ROLE_MENTOR)) {
            throw new UserNotFoundException("Mentor with id '%s' not found. Course is not saved".formatted(courseDto.getMentorId()));
        }

        Course course = courseMapper.toEntity(courseDto);

        System.out.println(course);
        course.setStatus(CourseStatus.CREATED); // set to CREATED by default when course persisted for the first time
        course.setSupervisorId(2L);
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
        var response = courseMapper.toResponseDto(courseById.orElseThrow(
                () -> new CourseNotFoundException("Course with provided id: '%s' not found".formatted(id))
        ));

        User userById = userService.findById(courseById.get().getMentorId());

        response.setMentorUsername(userById.getUsername()); // what if course exists but mentor deleted from a system

        return response;
    }

    @Override
    public List<CourseResponseDTO> findAllCourses(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Course> courses = courseRepository.findAllByDeletedAtIsNull(pageRequest);
        log.info("Total courses found: {}", courses.size());
        List<CourseResponseDTO> responseDTOS = new ArrayList<>();
        for (Course course : courses) {
            CourseResponseDTO responseDto = courseMapper.toResponseDto(course);

            User mentor = userService.findById(course.getMentorId());

            responseDto.setMentorUsername(mentor.getUsername());

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
        String mentorUsername = userService.findById(course.getMentorId()).getUsername();

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

        if (dto.getMentorId() != null && !dto.getMentorId().equals(course.getMentorId())) {
            if (userService.existsByIdAndRole(dto.getMentorId(), Role.ROLE_MENTOR)) {
                audit.auditChange(course.getId(), "UPDATED", "mentorId",
                        String.valueOf(course.getMentorId()), String.valueOf(dto.getMentorId()), null);
                course.setMentorId(dto.getMentorId());
                mentorUsername = userService.findById(dto.getMentorId()).getUsername();
                changed = true;
            }
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
        responseDTO.setMentorUsername(mentorUsername);

        return responseDTO;
    }

    @Override
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
        courseRepository.save(course);

        return new HashMap<>(Map.of(
                "id", id.toString(),
                "deletedAt", course.getDeletedAt().toString()
        ));
    }
}
