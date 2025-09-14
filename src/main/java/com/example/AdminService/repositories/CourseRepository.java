package com.example.AdminService.repositories;

import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.Program;
import com.example.AdminService.enums.CourseStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jdk.jfr.Name;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query(value = """
            SELECT * FROM courses c where c.program_id = :programId AND c.status <> :status
            """, nativeQuery = true
    )
    List<Course> findAllByProgram_IdAndStatusNot(@Param("programId") Long program_id, @Param("status") CourseStatus status);

    Page<Course> findAllByStatusNot(CourseStatus courseStatus, Pageable pageable);


    Optional<Course> findByIdAndStatusNot(Long id, CourseStatus courseStatus);

    Boolean existsByProgramAndNameAndStatusNot(Program program, String name, CourseStatus status);

    boolean existsByProgram_IdAndNameAndStatusNot(@NotNull(message = "Program ID is required") Long aLong, @NotBlank(message = "Name is required") String name, CourseStatus courseStatus);

    boolean existsCourseByIdAndStatusNot(Long id, CourseStatus status);
}
