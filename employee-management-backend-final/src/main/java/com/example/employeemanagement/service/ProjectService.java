package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.ProjectDTO;
import com.example.employeemanagement.exception.ResourceNotFoundException;

import java.util.List;

public interface ProjectService {
    ProjectDTO createProject(ProjectDTO projectDTO);
    ProjectDTO updateProject(Long id, ProjectDTO projectDTO);
    List<ProjectDTO> getAllProjects();
    ProjectDTO getProjectById(Long id);
    void deleteProject(Long id);
    ProjectDTO assignSeniorProjectManager(Long projectId, Long employeeId) throws ResourceNotFoundException;
}
