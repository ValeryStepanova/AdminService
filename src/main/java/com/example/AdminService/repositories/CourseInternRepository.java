package com.example.AdminService.repositories;

import com.example.AdminService.entities.CourseIntern;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseInternRepository extends JpaRepository<CourseIntern, Long> {
    @Query(value = "SELECT intern_id FROM courses_interns WHERE course_id = :courseId AND status = 'ASSIGNED'",
            countQuery = "SELECT COUNT(*) FROM courses_interns WHERE course_id = :courseId AND status = 'ASSIGNED'",
            nativeQuery = true)
    Page<UUID> findCourseInternByCourseId(Long courseId, PageRequest pageRequest);

    @Modifying
    @Query(
            value = """
                    UPDATE course_intern 
                    SET status = 'UNASSIGNED'
                    WHERE course_id = :courseId 
                      AND intern_id IN (:ids)
                    RETURNING intern_id
                    """,
            nativeQuery = true
    )
    List<UUID> unassign(@Param("courseId") Long courseId, @Param("ids") List<UUID> uuids);

}