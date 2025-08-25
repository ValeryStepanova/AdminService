package com.example.AdminService.repositories;

import com.example.AdminService.entities.Course;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Course c WHERE c.name = :name AND c.deletedAt IS NULL")
    boolean existsCourseByName(@Param("name") String name);


    Optional<Course> findCourseByIdAndDeletedAtIsNull(Long id);

    List<Course> findAllByDeletedAtIsNull(PageRequest pageRequest);
}
