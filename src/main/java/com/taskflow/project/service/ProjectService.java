
package com.taskflow.project.service;

import com.taskflow.project.dto.CreateProjectRequest;
import com.taskflow.project.dto.ProjectResponse;

import com.taskflow.project.model.Project;
import com.taskflow.project.model.ProjectMember;
import com.taskflow.project.model.Role;
import com.taskflow.project.model.User;
import com.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.project.repository.ProjectRepository;
import com.taskflow.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMemberRepository projectMemberRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository, ProjectMemberRepository projectMemberRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectMemberRepository = projectMemberRepository;
    }

    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request,String curruseremail) {

        User user = userRepository.findByEmail(curruseremail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + curruseremail));


        Project project = new Project(request.name(), request.description(), user);
        Project saved = projectRepository.save(project);
        ProjectMember creatorMember = new ProjectMember(saved, user, Role.ADMIN);
        projectMemberRepository.save(creatorMember);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponse> getAllProjects() {

        return projectRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(Long id) {

        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Project not found: " + id)
                );

        return toResponse(project);
    }

    private ProjectResponse toResponse(Project project) {

        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt(),
                project.getCreatedBy() != null ? project.getCreatedBy().getId() : null,
                project.getCreatedBy() != null ? project.getCreatedBy().getName() : null,
                project.getCreatedBy() != null ? project.getCreatedBy().getEmail() : null

        );
    }
}
