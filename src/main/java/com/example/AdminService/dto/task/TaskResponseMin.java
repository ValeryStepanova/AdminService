package com.example.AdminService.dto.task;

import com.example.AdminService.dto.course.CourseResponseMin;
import com.example.AdminService.entities.Task;
import com.example.AdminService.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseMin {
    private Long id;
    private CourseResponseMin course;
    private String title;
    private String definition;
    private TaskStatus status;


    public TaskResponseMin(Task task) {
        this.id = task.getId();
        this.course = new CourseResponseMin(task.getCourse());
        this.title = task.getTitle();
        this.definition = task.getDefinition();
        this.status = task.getStatus();
    }
}
