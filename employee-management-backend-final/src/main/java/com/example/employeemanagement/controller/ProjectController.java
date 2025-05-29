// ProjectController.java
package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.ProjectDTO;
import com.example.employeemanagement.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('SENIOR_PROJECT_MANAGER')")
    public ResponseEntity<ProjectDTO> createProject(@RequestBody ProjectDTO projectDTO) {
        ProjectDTO createdProject = projectService.createProject(projectDTO);
        return ResponseEntity.ok(createdProject);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('SENIOR_PROJECT_MANAGER')")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
        return ResponseEntity.ok(updatedProject);
    }

    @GetMapping
    @PreAuthorize("hasRole('admin') or hasRole('SENIOR_PROJECT_MANAGER') or hasRole('PROJECT_MANAGER') or hasRole('TEAM_MANAGER')")
    public ResponseEntity<List<ProjectDTO>> getAllProjects() {
        List<ProjectDTO> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or hasRole('SENIOR_PROJECT_MANAGER') or hasRole('PROJECT_MANAGER') or hasRole('TEAM_MANAGER')")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id) {
        ProjectDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/assign-spm/{employeeId}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ProjectDTO> assignSeniorProjectManager(
            @PathVariable Long projectId,
            @PathVariable Long employeeId) {
        ProjectDTO project = projectService.assignSeniorProjectManager(projectId, employeeId);
        return ResponseEntity.ok(project);
    }
}