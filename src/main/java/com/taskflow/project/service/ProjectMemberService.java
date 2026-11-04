package com.taskflow.project.service;

import com.taskflow.project.dto.AddMemberRequest;
import com.taskflow.project.dto.MemberResponse;
import com.taskflow.project.model.Project;
import com.taskflow.project.model.ProjectMember;
import com.taskflow.project.model.Role;
import com.taskflow.project.model.User;
import com.taskflow.project.repository.ProjectMemberRepository;
import com.taskflow.project.repository.ProjectRepository;
import com.taskflow.project.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
public class ProjectMemberService {

    private final ProjectMemberRepository projectMemberRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectMemberService(ProjectMemberRepository projectMemberRepository,
                                ProjectRepository projectRepository,
                                UserRepository userRepository) {
        this.projectMemberRepository = projectMemberRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    // RBAC Helper: Check if requesting user is an ADMIN of the project
    private void verifyAdminAccess(Long projectId, String requesterEmail) throws AccessDeniedException {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + requesterEmail));

        projectMemberRepository.findByProjectIdAndUserIdAndRole(projectId, requester.getId(), Role.ADMIN)
                .orElseThrow(() -> new AccessDeniedException("Access Denied: Only project ADMINs can perform this action"));
    }

    // RBAC Helper: Check if requesting user is a MEMBER or ADMIN of the project
    public void verifyMemberAccess(Long projectId, String requesterEmail) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new RuntimeException("User not found: " + requesterEmail));

        boolean isMember = projectMemberRepository.existsByProjectIdAndUserId(projectId, requester.getId());
        if (!isMember) {
            throw new RuntimeException("Access Denied: You are not a member of this project");
        }
    }

    @Transactional
    public MemberResponse addMemberToProject(Long projectId, AddMemberRequest request, String requesterEmail) throws AccessDeniedException {
        // 1. Verify caller has ADMIN permissions
        verifyAdminAccess(projectId, requesterEmail);

        // 2. Fetch target user by email
        User userToAdd = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User to invite not found with email: " + request.email()));

        // 3. Prevent duplicate memberships
        if (projectMemberRepository.existsByProjectIdAndUserId(projectId, userToAdd.getId())) {
            throw new RuntimeException("User is already a member of this project");
        }

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found with ID: " + projectId));

        ProjectMember member = new ProjectMember(project, userToAdd, request.role());
        ProjectMember savedMember = projectMemberRepository.save(member);

        return toMemberResponse(savedMember);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> getProjectMembers(Long projectId, String requesterEmail) {
        // Any member (ADMIN or MEMBER) can view the member list
        verifyMemberAccess(projectId, requesterEmail);

        return projectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(this::toMemberResponse)
                .toList();
    }

    @Transactional
    public void removeMemberFromProject(Long projectId, Long memberId, String requesterEmail) throws AccessDeniedException {
        verifyAdminAccess(projectId, requesterEmail);

        ProjectMember member = projectMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Member record not found"));

        if (!member.getProject().getId().equals(projectId)) {
            throw new AccessDeniedException("Member does not belong to this project");
        }

        projectMemberRepository.delete(member);
    }

    private MemberResponse toMemberResponse(ProjectMember member) {
        return new MemberResponse(
                member.getId(),
                member.getUser().getId(),
                member.getUser().getName(),
                member.getUser().getEmail(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}