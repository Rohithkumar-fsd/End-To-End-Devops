package com.taskflow.project.controller;

import com.taskflow.project.dto.AddMemberRequest;
import com.taskflow.project.dto.MemberResponse;
import com.taskflow.project.service.ProjectMemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/members")
public class ProjectMemberController {

    private final ProjectMemberService projectMemberService;

    public ProjectMemberController(ProjectMemberService projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    // POST /api/projects/{projectId}/members - Invite user by email (ADMIN only)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MemberResponse addMember(
            @PathVariable Long projectId,
            @Valid @RequestBody AddMemberRequest request,
            Principal principal) {
        return projectMemberService.addMemberToProject(projectId, request, principal.getName());
    }

    // GET /api/projects/{projectId}/members - List all members (Project Members only)
    @GetMapping
    public List<MemberResponse> getMembers(
            @PathVariable Long projectId,
            Principal principal) {
        return projectMemberService.getProjectMembers(projectId, principal.getName());
    }

    // DELETE /api/projects/{projectId}/members/{memberId} - Remove member (ADMIN only)
    @DeleteMapping("/{memberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMember(
            @PathVariable Long projectId,
            @PathVariable Long memberId,
            Principal principal) {
        projectMemberService.removeMemberFromProject(projectId, memberId, principal.getName());
    }
}