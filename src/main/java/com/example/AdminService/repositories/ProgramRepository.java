package com.example.AdminService.repositories;

import com.example.AdminService.entities.Program;
import com.example.AdminService.enums.ProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Boolean existsByNameAndStatusNot(String name, ProgramStatus status);
    Optional<Program> findByIdAndStatusNot(Long id, ProgramStatus status);
    List<Program> findAllByStatusNot(ProgramStatus courseStatus);

    @Query(value = """
                SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END
                FROM Program p
                WHERE p.id = :id AND p.status <> 'DELETED'
            """)
    boolean existsByIdAndStatusNotDeleted(@Param("id") Long id);
}
