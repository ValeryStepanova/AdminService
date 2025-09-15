package com.example.AdminService.service.impl;

import com.example.AdminService.client.AuthServiceClient;
import com.example.AdminService.dto.UserPrincipal;
import com.example.AdminService.dto.response.AssignInternsResponse;
import com.example.AdminService.dto.response.UserResponse;
import com.example.AdminService.dto.task.TaskResponse;
import com.example.AdminService.entities.*;
import com.example.AdminService.enums.ResponseStatus;
import com.example.AdminService.enums.Role;
import com.example.AdminService.enums.TaskStatus;
import com.example.AdminService.exception.ApiException;
import com.example.AdminService.exception.InternNotFoundException;
import com.example.AdminService.mapper.TaskMapper;
import com.example.AdminService.repositories.CourseMentorRepository;
import com.example.AdminService.repositories.ProgramExpertRepository;
import com.example.AdminService.repositories.TaskInternRepository;
import com.example.AdminService.repositories.TaskRepository;
import com.example.AdminService.utils.CurrentUserService;
import com.itechart.profileserviceapi.api.UserClient;
import com.itechart.profileserviceapi.dto.UserDto;
import com.itechart.profileserviceapi.dto.UserIdsRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.AdminService.enums.SpecialistProgramStatus.ASSIGNED;
import static com.example.AdminService.enums.SpecialistProgramStatus.UNASSIGNED;
import static com.example.AdminService.enums.TaskInternStatus.IN_PROGRESS;
import static com.example.AdminService.mapper.TaskInternMapper.toEntity;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskInternService {
    private final TaskInternRepository taskInternRepository;
    private final TaskRepository taskRepository;
    private final ProgramExpertRepository programExpertRepository;
    private final CourseMentorRepository courseMentorRepository;
    private final AuthServiceClient authServiceClient;
    private final UserClient userClient;

    @Transactional
    public AssignInternsResponse assignIntern(Long taskId, UserIdsRequest userIdsRequest) {
        Task task = taskRepository.findByIdAndStatusNot(taskId, TaskStatus.DELETED)
                .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));
        UserPrincipal currentUser = getCurrentUser();
        if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_SUPERVISOR.name())
                || currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_EXPERT.name())) {
            List<UserDto> interns = Optional.of(userClient.findAllByIds(userIdsRequest).getBody())
                    .orElseThrow(() -> new InternNotFoundException("Such interns are not found"));
            interns.forEach(userDto -> {
                log.info("User {} roles: {}", userDto.getUuid(), userDto.getRoles());
                if (!userDto.getRoles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_INTERN)) {
                    log.warn("User {} skipped — not an intern. Roles: {}", userDto.getUuid(), userDto.getRoles());
                }
            });

            var internIds = interns.stream()
                    .filter(userDto -> userDto.getRoles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_INTERN))
                    .map(UserDto::getUuid)
                    .toList();
            log.info("Interns to assign: {}", internIds);
            interns.forEach(userDto -> log.info("User {} roles: {}", userDto.getUuid(), userDto.getRoles()));

            List<TaskIntern> taskInterns = internIds.stream().map(
                    internId -> TaskIntern.builder()
                            .task(task)
                            .internId(internId)
                            .taskStatus(IN_PROGRESS)
                            .status(ASSIGNED)
                            .build()
            ).toList();
            taskInternRepository.saveAll(taskInterns);
            log.info("Saved {} TaskInterns", taskInterns.size());


        }
        List<UUID> internIds = taskInternRepository.findAllByTaskId(taskId)
                .stream()
                .map(TaskIntern::getInternId)
                .toList();
        return new AssignInternsResponse(taskId, task.getTitle(), internIds);
    }

//    @Transactional
//    public TaskResponse assignIntern(Long taskId, UUID internId) {
//        Task task = taskRepository.findByIdAndStatusNot(taskId, TaskStatus.DELETED)
//            .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));
//
//        UserPrincipal currentUser = getCurrentUser();
//        Course course = task.getCourse();
//
//        if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_SUPERVISOR.name())) {
//            assign(task, internId);
//        } else if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_EXPERT.name())) {
//            // Check if expert is assigned to the program
//            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.uuid(), ASSIGNED))
//                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
//
//            assign(task, internId);
//        } else {
//            // Check if mentor is assigned to the course
//            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.uuid(), ASSIGNED))
//                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);
//
//            UserResponse intern = getIntern(internId);
//            taskInternRepository.saveAndFlush(toEntity(task, intern, TRUE));
//            // TODO: send approval request to expert to assign the intern
//        }
//
//    List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
//    List<TaskIntern> interns = taskInternRepository.findAllByTaskId(task.getId());
//
//        return TaskMapper.toResponse(task,course,mentors,interns);
//}

    @Transactional
    public TaskResponse unassignIntern(Long taskId, UUID internId) {
        Task task = taskRepository.findByIdAndStatusNot(taskId, TaskStatus.DELETED)
                .orElseThrow(() -> new ApiException(ResponseStatus.TASK_NOT_FOUND));

        UserPrincipal currentUser = getCurrentUser();
        Course course = task.getCourse();

        if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_SUPERVISOR.name())) {
            TaskIntern taskIntern = taskInternRepository.findByTask_IdAndInternId(task.getId(), internId)
                    .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (taskIntern.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            taskIntern.setStatus(UNASSIGNED);
            taskInternRepository.saveAndFlush(taskIntern);
        } else if (currentUser.roles().contains(com.itechart.profileserviceapi.enums.Role.ROLE_EXPERT.name())) {
            // Check if expert is assigned to the program
            if (!programExpertRepository.existsByProgramAndExpertIdAndStatus(course.getProgram(), currentUser.uuid(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            TaskIntern taskIntern = taskInternRepository.findByTask_IdAndInternId(task.getId(), internId)
                    .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (taskIntern.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            taskIntern.setStatus(UNASSIGNED);
            taskInternRepository.saveAndFlush(taskIntern);
        } else {
            // Check if mentor is assigned to the course
            if (!courseMentorRepository.existsByCourseAndMentorIdAndStatus(course, currentUser.uuid(), ASSIGNED))
                throw new ApiException(ResponseStatus.METHOD_NOT_ALLOWED);

            TaskIntern taskIntern = taskInternRepository.findByTask_IdAndInternId(task.getId(), internId)
                    .orElseThrow(() -> new ApiException(ResponseStatus.SPECIALIST_NOT_FOUND));

            if (taskIntern.getStatus().equals(UNASSIGNED))
                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_UNASSIGNED);

            // TODO: send approval request to expert to unassign the intern
        }

        List<CourseMentor> mentors = courseMentorRepository.findAllByCourseId(course.getId());
        List<TaskIntern> interns = taskInternRepository.findAllByTaskId(task.getId());

        return TaskMapper.toResponse(task, course, mentors, interns);
    }

//    private void assign(Task task, UUID internId) {
//        Optional<TaskIntern> optionalTaskIntern = taskInternRepository.findByTask_IdAndInternId(task.getId(), internId);
//        if (optionalTaskIntern.isEmpty()) {
//            UserResponse intern = getIntern(internId);
//            taskInternRepository.saveAndFlush(toEntity(task, intern, FALSE));
//        } else {
//            TaskIntern taskIntern = optionalTaskIntern.get();
//            if (taskIntern.getStatus().equals(ASSIGNED))
//                throw new ApiException(ResponseStatus.SPECIALIST_ALREADY_ASSIGNED);
//
//            taskIntern.setStatus(ASSIGNED);
//            taskInternRepository.saveAndFlush(taskIntern);
//        }
//    }

    private UserPrincipal getCurrentUser() {
        UserPrincipal currentUser = CurrentUserService.getCurrentUser();
        if (currentUser == null) {
            log.error("User details are not present in the context in CourseService.getCurrentUser()");
            throw new ApiException(ResponseStatus.REQUEST_PARAMETER_NOT_FOUND);
        }

        return currentUser;
    }

    private UserResponse getIntern(UUID internId) {
        UserResponse intern = authServiceClient.getUserById(internId);
        if (!intern.getRole().equals(Role.INTERN))
            throw new ApiException(ResponseStatus.ROLE_MISMATCH_EXCEPTION);

        return intern;
    }
}
