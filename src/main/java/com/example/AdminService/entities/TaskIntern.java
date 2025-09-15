package com.example.AdminService.entities;

import com.example.AdminService.enums.SpecialistProgramStatus;
import com.example.AdminService.enums.TaskInternStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Getter
@Setter
@Table(name = "tasks_interns", uniqueConstraints = {@UniqueConstraint(columnNames = {"task_id", "intern_id"})})
@Entity
public class TaskIntern extends BaseEntity {
    @Id
    //@SequenceGenerator(name = "task_interns_id_seq", sequenceName = "task_interns_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.IDENTITY)//, generator = "task_interns_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @Column(name = "intern_id", nullable = false)
    private UUID internId;


    @Column(name = "github_link")
    private String githubLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "task_status", nullable = false)
    private TaskInternStatus taskStatus = TaskInternStatus.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SpecialistProgramStatus status = SpecialistProgramStatus.ASSIGNED;

    @Override
    public void softDelete(UUID currentUserId) {

    }
}
