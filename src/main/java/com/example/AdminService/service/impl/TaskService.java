package com.example.AdminService.service.impl;

import com.example.AdminService.dto.UserPrincipal;
import com.example.AdminService.dto.task.TaskRequest;
import com.example.AdminService.dto.task.TaskResponse;
import com.example.AdminService.dto.task.TaskResponseMin;
import com.example.AdminService.dto.task.TaskUpdateRequest;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseMentor;
import com.example.AdminService.entities.Task;
import com.example.AdminService.entities.TaskIntern;
import com.example.AdminService.enums.*;
import com.example.AdminService.exception.ApiException;
import com.example.AdminService.mapper.CourseMentorMapper;
import com.example.AdminService.mapper.TaskMapper;
import com.example.AdminService.repositories.*;
import com.example.AdminService.utils.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.AdminService.mapper.TaskMapper.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final TaskInternRepository taskInternRepository;
    private final CourseRepository courseRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final ProgramExpertRepository programExpertRepository;


    public Page<TaskResponseMin> getAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Task> pageTask = taskRepository.findAllByStatusNot(TaskStatus.DELETED, pageRequest);
        return pageTask.map(TaskResponseMin::new);
    }

    public Page<TaskResponseMin> getAllByCourseId(int page, int size, Long courseId) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Task> pageTask = taskRepository.findAllByCourse_IdAndStatusNot(pageRequest, courseId, TaskStatus.DELETED);
        return pageTask.map(TaskResponseMin::new);
    }

    public TaskResponse getById(Long id) {
        Task task = taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
                .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        Course course = task.getCourse();
        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
        List<TaskIntern> interns = taskInternRepository.findAllByTaskId(task.getId());

        return toResponse(task, course, mentors, interns);
    }

    @Transactional
    public TaskResponse create(TaskRequest request) {
        Course course = courseRepository.findByIdAndStatusNot(request.courseId(), CourseStatus.DELETED)
                .orElseThrow(() -> new ApiException(ResponseStatus.COURSE_NOT_FOUND));

        if (taskRepository.existsByCourse_IdAndTitleAndStatusNot(request.courseId(), request.title(), TaskStatus.DELETED))
            throw new ApiException(ResponseStatus.TASK_ALREADY_EXISTS);

        UserPrincipal currentUser = getCurrentUser();

        if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_SUPERVISOR.name())) {
            Task task = taskRepository.save(toEntity(request, course, FALSE));
            return toResponse(task, course, List.of(), List.of());
        } else if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_EXPERT.name())) {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.uuid(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            Task task = taskRepository.save(toEntity(request, course, FALSE));
            return toResponse(task, course, List.of(), List.of());
        } else {
            // Check if mentor is assigned to the course
            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.uuid(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            Task task = taskRepository.save(toEntity(request, course, TRUE));
            // CourseMentor courseMentor = courseMentorRepository.save(CourseMentorMapper.toEntity(course, currentUser));
            // TODO: send approval request to expert to create the task
            return toResponse(task, course, List.of(/*courseMentor*/), List.of());
        }
    }

    public TaskResponse updateById(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
                .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        if (taskRepository.existsByCourse_IdAndTitleAndStatusNot(task.getCourse().getId(), request.title(), TaskStatus.DELETED))
            throw new ApiException(ResponseStatus.TASK_ALREADY_EXISTS);

        UserPrincipal currentUser = getCurrentUser();
        Course course = task.getCourse();

        if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_SUPERVISOR.name())) {
            task.setTitle(request.title());
            task.setDefinition(request.definition());
            //update(request, task);
            log.info("Updating task {}: title='{}', definition='{}'", task.getId(), task.getTitle(), task.getDefinition());
            taskRepository.save(task);
        } else if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_EXPERT.name())) {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.uuid(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
            task.setTitle(request.title());
            task.setDefinition(request.definition());
            //update(request, task);
            log.info("Updating task {}: title='{}', definition='{}'", task.getId(), task.getTitle(), task.getDefinition());
            taskRepository.save(task);

        } else {
            // Check if mentor is assigned to the course
            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.uuid(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            // TODO: send approval request to expert to update the task
        }

        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
        List<TaskIntern> interns = taskInternRepository.findAllByTaskId(task.getId());

        return toResponse(task, course, mentors, interns);
    }

    public void deleteById(Long id) {
        Task task = taskRepository.findByIdAndStatusNot(id, TaskStatus.DELETED)
                .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        UserPrincipal currentUser = getCurrentUser();
        Course course = task.getCourse();

        if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_EXPERT.name())) {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.uuid(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
        } else if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_MENTOR.name())) {
            // Check if mentor is assigned to the course
            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.uuid(), SpecialistProgramStatus.ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            // TODO: send approval request to expert to delete the task
            return;
        }

        task.softDelete(currentUser.uuid());
        taskRepository.save(task);
    }

    private UserPrincipal getCurrentUser() {
        UserPrincipal currentUser = CurrentUserService.getCurrentUser();
        if (currentUser == null) {
            log.error("User details are not present in the context in CourseService.getCurrentUser()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        return currentUser;
    }
}
