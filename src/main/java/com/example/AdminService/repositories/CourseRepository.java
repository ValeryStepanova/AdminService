package com.example.AdminService.repositories;

import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.Program;
import com.example.AdminService.enums.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByProgram_IdAndStatusNot(Long program_id, CourseStatus status);
    List<Course> findAllByStatusNot(CourseStatus courseStatus);
    Optional<Course> findByIdAndStatusNot(Long id, CourseStatus courseStatus);
    Boolean existsByProgramAndNameAndStatusNot(Program program, String name, CourseStatus status);
}
