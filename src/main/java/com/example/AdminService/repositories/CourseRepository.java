package com.example.AdminService.repositories;

import com.example.AdminService.entities.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Course c WHERE c.name = :name AND c.deletedAt IS NULL")
    boolean existsCourseByName(@Param("name") String name);


    Optional<Course> findCourseByIdAndDeletedAtIsNull(Long id);

    List<Course> findAllByDeletedAtIsNull(PageRequest pageRequest);

    @Query(value = "SELECT intern_id FROM course_interns WHERE course_id = :courseId",
            countQuery = "SELECT COUNT(*) FROM course_interns WHERE course_id = :courseId",
            nativeQuery = true)
    Page<UUID> findInternIdsByCourseId(@Param("courseId") Long courseId, Pageable pageable);


    @Query(value = """
            SELECT mentor_id
            FROM course_mentors
            WHERE course_id = :courseId
            """, nativeQuery = true)
    List<UUID> findMentorsByCourseId(@Param("courseId") Long courseId);


    @Query(value = """
            DELETE
            FROM course_mentors
            WHERE course_id = :courseId
            """, nativeQuery = true)
    void deleteMentorRelationsByCourse(@Param("courseId") Long id);

    @Query(value = """
            DELETE
            FROM course_interns
            WHERE course_id = :courseId
            """, nativeQuery = true)
    void deleteInternRelationsByCourse(@Param("courseId") Long id);

    @Query(value = """
            DELETE FROM course_interns
            WHERE course_id = :courseId
              AND intern_id IN (:internIds)
            RETURNING intern_id
            """, nativeQuery = true)
    List<UUID> unassignInterns(@Param("courseId") Long courseId,
                               @Param("internIds") List<UUID> internIds);


    @Query(value = """
            DELETE FROM course_mentors
            WHERE course_id = :courseId
              AND mentor_id IN (:mentorIds)
            RETURNING mentor_id
            """, nativeQuery = true)
    List<UUID> unassignMentors(@Param("courseId") Long courseId,
                               @Param("mentorIds") List<UUID> mentorIds);

}
