package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.TeamDTO;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.model.Project;
import com.example.employeemanagement.model.Role;
import com.example.employeemanagement.model.Team;
import com.example.employeemanagement.repository.TeamRepository;
import com.example.employeemanagement.repository.ProjectRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final ModelMapper modelMapper;
    private static final Logger log = LoggerFactory.getLogger(TeamServiceImpl.class);

    public TeamServiceImpl(TeamRepository teamRepository, 
                         ProjectRepository projectRepository,
                         EmployeeRepository employeeRepository, 
                         ModelMapper modelMapper) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.employeeRepository = employeeRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());

        Project project = projectRepository.findById(teamDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        team.setProject(project);

        if (teamDTO.getProjectManagerId() != null) {
            Employee pm = employeeRepository.findById(teamDTO.getProjectManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project Manager not found"));
            
            if (!pm.getRole().equals(Role.PROJECT_MANAGER)) {
                throw new IllegalArgumentException("Employee must be a Project Manager");
            }
            
            team.setProjectManager(pm);
        }

        if (teamDTO.getTeamManagerId() != null) {
            Employee tm = employeeRepository.findById(teamDTO.getTeamManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team Manager not found"));
            
            if (!tm.getRole().equals(Role.TEAM_MANAGER)) {
                throw new IllegalArgumentException("Employee must be a Team Manager");
            }
            
            team.setTeamManager(tm);
        }

        Team savedTeam = teamRepository.save(team);
        return convertToDTO(savedTeam);
    }

    @Override
    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        Team existingTeam = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        existingTeam.setName(teamDTO.getName());

        Project project = projectRepository.findById(teamDTO.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        existingTeam.setProject(project);

        if (teamDTO.getProjectManagerId() != null) {
            Employee pm = employeeRepository.findById(teamDTO.getProjectManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project Manager not found"));
            
            if (!pm.getRole().equals(Role.PROJECT_MANAGER)) {
                throw new IllegalArgumentException("Employee must be a Project Manager");
            }
            
            existingTeam.setProjectManager(pm);
        } else {
            existingTeam.setProjectManager(null);
        }

        if (teamDTO.getTeamManagerId() != null) {
            Employee tm = employeeRepository.findById(teamDTO.getTeamManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team Manager not found"));
            
            if (!tm.getRole().equals(Role.TEAM_MANAGER)) {
                throw new IllegalArgumentException("Employee must be a Team Manager");
            }
            
            existingTeam.setTeamManager(tm);
        } else {
            existingTeam.setTeamManager(null);
        }

        Team updatedTeam = teamRepository.save(existingTeam);
        return convertToDTO(updatedTeam);
    }

    @Override
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        log.info("Team before mapping: {}", team);
        log.info("Project: {}", team.getProject());
        log.info("Project Manager: {}", team.getProjectManager());
        log.info("Team Manager: {}", team.getTeamManager());
        
        return convertToDTO(team);
    }

    @Override
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        teamRepository.delete(team);
    }

    @Override
    public TeamDTO assignProjectManager(Long teamId, Long employeeId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getRole().equals(Role.PROJECT_MANAGER)) {
            throw new IllegalArgumentException("Employee must be a Project Manager");
        }

        team.setProjectManager(employee);
        Team updatedTeam = teamRepository.save(team);
        return convertToDTO(updatedTeam);
    }

    @Override
    public TeamDTO assignTeamManager(Long teamId, Long employeeId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getRole().equals(Role.TEAM_MANAGER)) {
            throw new IllegalArgumentException("Employee must be a Team Manager");
        }

        team.setTeamManager(employee);
        Team updatedTeam = teamRepository.save(team);
        return convertToDTO(updatedTeam);
    }

    @Override
    public List<EmployeeDTO> getTeamMembers(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));
        
        // Initialize members to avoid LazyInitializationException
        Hibernate.initialize(team.getMembers());
        
        return team.getMembers().stream()
                .map(employee -> modelMapper.map(employee, EmployeeDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public TeamDTO addTeamMember(Long teamId, Long employeeId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getRole().equals(Role.MEMBER)) {
            throw new IllegalArgumentException("Employee must be a Member");
        }

        if (team.getMembers().size() >= 6) {
            throw new IllegalStateException("A team can have at most 6 members");
        }

        team.addMember(employee);
        Team updatedTeam = teamRepository.save(team);
        return convertToDTO(updatedTeam);
    }

    @Override
    public void removeTeamMember(Long teamId, Long employeeId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found"));

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        team.removeMember(employee);
        teamRepository.save(team);
    }

    private TeamDTO convertToDTO(Team team) {
        TeamDTO teamDTO = modelMapper.map(team, TeamDTO.class);
        
        // Manually map the fields that aren't being mapped automatically
        if (team.getProject() != null) {
            teamDTO.setProjectId(team.getProject().getId());
            teamDTO.setProjectName(team.getProject().getName());
        }
        
        if (team.getProjectManager() != null) {
            teamDTO.setProjectManagerId(team.getProjectManager().getEmployeeId());
            teamDTO.setProjectManagerName(
                team.getProjectManager().getFirstName() + " " + 
                team.getProjectManager().getLastName()
            );
        }
        
        if (team.getTeamManager() != null) {
            teamDTO.setTeamManagerId(team.getTeamManager().getEmployeeId());
            teamDTO.setTeamManagerName(
                team.getTeamManager().getFirstName() + " " + 
                team.getTeamManager().getLastName()
            );
        }
        
        return teamDTO;
    }
}