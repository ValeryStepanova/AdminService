package com.example.AdminService.repositories;

import com.example.AdminService.entities.Task;
import com.example.AdminService.enums.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Boolean existsByCourse_IdAndTitleAndStatusNot(Long course_id, String title, TaskStatus status);
    Optional<Task> findByIdAndStatusNot(Long id, TaskStatus status);
    Page<Task> findAllByStatusNot(TaskStatus status, PageRequest pageRequest);
    Page<Task> findAllByCourse_IdAndStatusNot(PageRequest pageRequest, Long courseId, TaskStatus taskInternStatus);
}
