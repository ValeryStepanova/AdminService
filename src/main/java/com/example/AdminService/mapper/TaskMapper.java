package com.example.AdminService.mapper;

import com.example.AdminService.dto.task.TaskRequest;
import com.example.AdminService.dto.task.TaskResponse;
import com.example.AdminService.dto.task.TaskResponseMin;
import com.example.AdminService.dto.task.TaskUpdateRequest;
import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseMentor;
import com.example.AdminService.entities.Task;
import com.example.AdminService.entities.TaskIntern;
import com.example.AdminService.enums.TaskStatus;
import org.mapstruct.Mapper;
import org.springframework.beans.BeanUtils;

import java.util.List;

public class TaskMapper {

    public static List<TaskResponseMin> toResponseList(List<Task> tasks) {
        return tasks.stream().map(TaskResponseMin::new).toList();
    }

    public static Task toEntity(TaskRequest request, Course course, Boolean isMentor) {
        Task task = new Task();
        BeanUtils.copyProperties(request, task, "courseId");
        task.setCourse(course);
        if (isMentor)
            task.setStatus(TaskStatus.PENDING_APPROVAL);
        else
            task.setStatus(TaskStatus.ACTIVE);

        return task;
    }

    public static TaskResponse toResponse(Task task, Course course, List<CourseMentor> mentors, List<TaskIntern> interns) {
        return new TaskResponse(task, course, mentors, interns);
    }

    public static void update(TaskUpdateRequest request, Task task) {
        BeanUtils.copyProperties(request, task);
    }

}
