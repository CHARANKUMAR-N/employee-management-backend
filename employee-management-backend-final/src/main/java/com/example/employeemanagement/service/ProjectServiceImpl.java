// ProjectServiceImpl.java
package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.ProjectDTO;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.model.Project;
import com.example.employeemanagement.model.Role;
import com.example.employeemanagement.repository.ProjectRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

	private final ProjectRepository projectRepository;
	private final EmployeeRepository employeeRepository;
	private final ModelMapper modelMapper;

	@Autowired
	public ProjectServiceImpl(ProjectRepository projectRepository, EmployeeRepository employeeRepository,
			ModelMapper modelMapper) {
		this.projectRepository = projectRepository;
		this.employeeRepository = employeeRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public ProjectDTO createProject(ProjectDTO projectDTO) {
		Project project = new Project();
		project.setName(projectDTO.getName());

		if (projectDTO.getSeniorProjectManagerId() != null) {
			Employee seniorPM = employeeRepository.findById(projectDTO.getSeniorProjectManagerId())
					.orElseThrow(() -> new ResourceNotFoundException(
							"Senior Project Manager not found with id: " + projectDTO.getSeniorProjectManagerId()));

			if (!seniorPM.getRole().equals(Role.SENIOR_PROJECT_MANAGER)) {
				throw new IllegalArgumentException("Employee must be a Senior Project Manager");
			}

			project.setSeniorProjectManager(seniorPM);
		}

		Project savedProject = projectRepository.save(project);
		return convertToDTO(savedProject);
	}

	@Override
	public ProjectDTO updateProject(Long id, ProjectDTO projectDTO) {
		Project existingProject = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

		existingProject.setName(projectDTO.getName());

		if (projectDTO.getSeniorProjectManagerId() != null) {
			Employee spm = employeeRepository.findById(projectDTO.getSeniorProjectManagerId())
					.orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

			if (!spm.getRole().equals(Role.SENIOR_PROJECT_MANAGER)) {
				throw new IllegalArgumentException("Employee must be a Senior Project Manager");
			}

			existingProject.setSeniorProjectManager(spm);
		} else {
			existingProject.setSeniorProjectManager(null);
		}

		Project updatedProject = projectRepository.save(existingProject);
		return convertToDTO(updatedProject);
	}

	@Override
	public List<ProjectDTO> getAllProjects() {
		return projectRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	@Override
	public ProjectDTO getProjectById(Long id) {
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
		return convertToDTO(project);
	}

	@Override
	public void deleteProject(Long id) {
		Project project = projectRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
		projectRepository.delete(project);
	}

	@Override
	public ProjectDTO assignSeniorProjectManager(Long projectId, Long employeeId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

		Employee employee = employeeRepository.findById(employeeId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

		if (!employee.getRole().equals(Role.SENIOR_PROJECT_MANAGER)) {
			throw new IllegalArgumentException("Employee must be a Senior Project Manager");
		}

		project.setSeniorProjectManager(employee);
		Project updatedProject = projectRepository.save(project);
		return convertToDTO(updatedProject);
	}

	private ProjectDTO convertToDTO(Project project) {
		ProjectDTO dto = new ProjectDTO();
		dto.setId(project.getId());
		dto.setName(project.getName());
		dto.setTeamsCount(project.getTeams().size());

		if (project.getSeniorProjectManager() != null) {
			dto.setSeniorProjectManagerId(project.getSeniorProjectManager().getEmployeeId());
			dto.setSeniorProjectManagerName(project.getSeniorProjectManager().getFirstName() + " "
					+ project.getSeniorProjectManager().getLastName());
		}

		return dto;
	}
}