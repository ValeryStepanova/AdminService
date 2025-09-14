package com.example.AdminService.mapper;

import com.example.AdminService.dto.response.UserResponse;
import com.example.AdminService.entities.Task;
import com.example.AdminService.entities.TaskIntern;
import com.example.AdminService.enums.SpecialistProgramStatus;

public class TaskInternMapper {

    public static TaskIntern toEntity(Task task, UserResponse intern, Boolean isMentor) {
        TaskIntern taskIntern = new TaskIntern();
        taskIntern.setTask(task);
        taskIntern.setInternId(intern.getId());
        taskIntern.setUsername(intern.getUsername());
        if (isMentor) taskIntern.setStatus(SpecialistProgramStatus.UNASSIGNED);

        return taskIntern;
    }

}
