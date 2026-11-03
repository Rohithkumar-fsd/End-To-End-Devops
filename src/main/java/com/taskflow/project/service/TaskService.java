package com.taskflow.project.service;

import com.taskflow.project.dto.CreateTaskRequest;
import com.taskflow.project.dto.TaskResponse;
import com.taskflow.project.dto.UpdateTaskStatusRequest;
import com.taskflow.project.model.Project;
import com.taskflow.project.model.Task;
import com.taskflow.project.model.TaskPriority;
import com.taskflow.project.model.TaskStatus;
import com.taskflow.project.repository.ProjectRepository;
import com.taskflow.project.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        com.taskflow.project.model.Project project = projectRepository.findById(request.projectId())
                .orElseThrow(() -> new RuntimeException("Project not found with id: " + request.projectId()));

        TaskStatus status = request.status() != null ? request.status() : TaskStatus.TODO;
        TaskPriority priority = request.priority() != null ? request.priority() : TaskPriority.MEDIUM;

        Task task = new Task(
                request.title(),
                request.description(),
                status,
                priority,
                project
        );

        Task savedTask = taskRepository.save(task);
        return toResponse(savedTask);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasksByProjectId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new RuntimeException("Project not found with id: " + projectId);
        }
        return taskRepository.findByProjectId(projectId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TaskResponse updateTaskStatus(Long taskId, UpdateTaskStatusRequest request) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        task.setStatus(request.status());
        Task updatedTask = taskRepository.save(task);
        return toResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        if (!taskRepository.existsById(taskId)) {
            throw new RuntimeException("Task not found with id: " + taskId);
        }
        taskRepository.deleteById(taskId);
    }

    private TaskResponse toResponse(Task task) {
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getProject().getId(),
                task.getAssignedTo() != null ? task.getAssignedTo().getName() : null,
                task.getCreatedAt(),
                task.getAssignedTo() != null ? task.getAssignedTo().getId() : null

        );
    }
}