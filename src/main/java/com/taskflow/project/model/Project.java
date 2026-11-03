
package com.taskflow.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    private LocalDateTime createdAt=LocalDateTime.now();

    public Project(@NotBlank(message = "Project name is required") @Size(max = 150, message = "Project name cannot exceed 150 characters") String name, @Size(max = 2000, message = "Description cannot exceed 2000 characters") String description, User user) {
        this.name=name;
        this.description=description;
        this.createdBy=user;
    }
}
