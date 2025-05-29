package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.TeamDTO;
import com.example.employeemanagement.exception.ResourceNotFoundException;

import java.util.List;

public interface TeamService {
    TeamDTO createTeam(TeamDTO teamDTO);
    TeamDTO updateTeam(Long id, TeamDTO teamDTO);
    List<TeamDTO> getAllTeams();
    TeamDTO getTeamById(Long id);
    void deleteTeam(Long id);
    TeamDTO assignProjectManager(Long teamId, Long employeeId) throws ResourceNotFoundException;
    TeamDTO assignTeamManager(Long teamId, Long employeeId) throws ResourceNotFoundException;
    List<EmployeeDTO> getTeamMembers(Long teamId) throws ResourceNotFoundException;
    TeamDTO addTeamMember(Long teamId, Long employeeId) throws ResourceNotFoundException;
    void removeTeamMember(Long teamId, Long employeeId) throws ResourceNotFoundException;
}