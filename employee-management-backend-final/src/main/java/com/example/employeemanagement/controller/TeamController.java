package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.TeamDTO;
import com.example.employeemanagement.service.TeamService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        return ResponseEntity.ok(teamService.getAllTeams());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@RequestBody TeamDTO teamDTO) {
        TeamDTO createdTeam = teamService.createTeam(teamDTO);
        return ResponseEntity.created(URI.create("/api/teams/" + createdTeam.getId()))
                .body(createdTeam);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(@PathVariable Long id, @RequestBody TeamDTO teamDTO) {
        return ResponseEntity.ok(teamService.updateTeam(id, teamDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{teamId}/project-manager/{employeeId}")
    public ResponseEntity<TeamDTO> assignProjectManager(
            @PathVariable Long teamId,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(teamService.assignProjectManager(teamId, employeeId));
    }

    @PostMapping("/{teamId}/team-manager/{employeeId}")
    public ResponseEntity<TeamDTO> assignTeamManager(
            @PathVariable Long teamId,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(teamService.assignTeamManager(teamId, employeeId));
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<EmployeeDTO>> getTeamMembers(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }

    @PostMapping("/{teamId}/add-member/{employeeId}")
    public ResponseEntity<TeamDTO> addTeamMember(
            @PathVariable Long teamId,
            @PathVariable Long employeeId) {
        return ResponseEntity.ok(teamService.addTeamMember(teamId, employeeId));
    }

    @DeleteMapping("/{teamId}/remove-member/{employeeId}")
    public ResponseEntity<Void> removeTeamMember(
            @PathVariable Long teamId,
            @PathVariable Long employeeId) {
        teamService.removeTeamMember(teamId, employeeId);
        return ResponseEntity.noContent().build();
    }
}