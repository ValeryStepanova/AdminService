package com.example.AdminService.repositories;

import com.example.AdminService.entities.Program;
import com.example.AdminService.enums.ProgramStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Long> {
    Boolean existsByNameAndStatusNot(String name, ProgramStatus status);
    Optional<Program> findByIdAndStatusNot(Long id, ProgramStatus status);
    List<Program> findAllByStatusNot(ProgramStatus courseStatus);
}
