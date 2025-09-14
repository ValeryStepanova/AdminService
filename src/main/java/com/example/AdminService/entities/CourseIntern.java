package com.example.AdminService.entities;

import com.example.AdminService.enums.SpecialistProgramStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Table(name = "courses_interns", uniqueConstraints = @UniqueConstraint(columnNames = {"course_id", "intern_id"}))
@Entity
public class CourseIntern extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "intern_id", nullable = false)
    private UUID internId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'ASSIGNED'")
    private SpecialistProgramStatus status = SpecialistProgramStatus.ASSIGNED;

    @Override
    public void softDelete(UUID currentUserId) {

    }
}
