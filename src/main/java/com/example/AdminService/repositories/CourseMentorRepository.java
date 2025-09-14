package com.example.AdminService.repositories;

import com.example.AdminService.entities.Course;
import com.example.AdminService.entities.CourseMentor;
import com.example.AdminService.enums.SpecialistProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseMentorRepository extends JpaRepository<CourseMentor, Long> {
    @Query(value = """
                SELECT cm FROM CourseMentor cm
                WHERE cm.course.id = :courseId AND cm.status = 'ASSIGNED'
            """)
    List<CourseMentor> findAllByCourseId(@PathVariable("courseId") Long courseId);

    Optional<CourseMentor> findByCourse_IdAndMentorId(Long course_id, UUID mentorId);

    Boolean existsByCourseAndMentorIdAndStatus(Course course, UUID mentorId, SpecialistProgramStatus status);


    @Query(value = """
            SELECT c.mentor_id FROM courses_mentors c WHERE c.course_id = :courseId AND c.status = 'ASSIGNED'
            """, nativeQuery = true)
    List<UUID> findCourseMentorsByCourseId(@Param("courseId") Long courseId);

    @Modifying
    @Query(
            value = """
            UPDATE course_mentor 
            SET status = 'UNASSIGNED'
            WHERE course_id = :courseId 
              AND mentor_id IN (:ids)
            RETURNING mentor_id
            """,
            nativeQuery = true
    )
    List<UUID> unassign(@Param("courseId") Long courseId, @Param("ids") List<UUID> uuids);

}
