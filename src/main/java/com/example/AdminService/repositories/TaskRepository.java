package com.example.AdminService.repositories;

import com.example.AdminService.entities.Task;
import com.example.AdminService.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Boolean existsByCourse_IdAndTitleAndStatusNot(Long course_id, String title, TaskStatus status);
    Optional<Task> findByIdAndStatusNot(Long id, TaskStatus status);
    List<Task> findAllByStatusNot(TaskStatus status);
    List<Task> findAllByCourse_IdAndStatusNot(Long courseId, TaskStatus taskInternStatus);
}
