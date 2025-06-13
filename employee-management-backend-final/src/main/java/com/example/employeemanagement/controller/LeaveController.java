package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.LeaveDTO;
import com.example.employeemanagement.model.Leave.LeaveStatus;
import com.example.employeemanagement.service.LeaveService;
import com.example.employeemanagement.service.SecurityService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final LeaveService leaveService;
    private final SecurityService securityService;

    public LeaveController(LeaveService leaveService, SecurityService securityService) {
        this.leaveService = leaveService;
        this.securityService = securityService;
    }

    @PostMapping
    @PreAuthorize("hasRole('admin') or hasRole('user')")
    public ResponseEntity<LeaveDTO> applyLeave(@RequestBody LeaveDTO leaveDTO, @AuthenticationPrincipal Jwt jwt) {
        if (!securityService.isAdmin()) {
            Long employeeId = securityService.getEmployeeIdFromToken(jwt);
            if (!employeeId.equals(leaveDTO.getEmployeeId())) {
                throw new AccessDeniedException("You can only apply leave for yourself");
            }
        }
        return ResponseEntity.ok(leaveService.applyLeave(leaveDTO));
    }

    @GetMapping("/employee/{employeeId}")
    @PreAuthorize("hasRole('admin') or hasRole('user')")
    public ResponseEntity<List<LeaveDTO>> getLeavesByEmployeeId(
            @PathVariable Long employeeId, 
            @AuthenticationPrincipal Jwt jwt) {
        if (!securityService.isAdmin()) {
            Long currentEmployeeId = securityService.getEmployeeIdFromToken(jwt);
            if (!currentEmployeeId.equals(employeeId)) {
                throw new AccessDeniedException("You can only view your own leaves");
            }
        }
        return ResponseEntity.ok(leaveService.getLeavesByEmployeeId(employeeId));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<LeaveDTO>> getPendingLeaves() {
        return ResponseEntity.ok(leaveService.getPendingLeaves());
    }

    @GetMapping("/pending/count")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Long> getPendingLeavesCount() {
        return ResponseEntity.ok(leaveService.countPendingLeaves());
    }

    @PutMapping("/{leaveId}/status")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<LeaveDTO> updateLeaveStatus(
            @PathVariable Long leaveId,
            @RequestParam LeaveStatus status,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(leaveService.updateLeaveStatus(leaveId, status, reason));
    }

    @GetMapping("/{leaveId}")
    @PreAuthorize("hasRole('admin') or hasRole('user')")
    public ResponseEntity<LeaveDTO> getLeaveById(
            @PathVariable Long leaveId,
            @AuthenticationPrincipal Jwt jwt) {
        if (!securityService.hasLeaveAccess(leaveId, jwt)) {
            throw new AccessDeniedException("You don't have permission to access this leave");
        }
        return ResponseEntity.ok(leaveService.getLeaveById(leaveId));
    }

    @PutMapping("/{leaveId}/cancel")
    @PreAuthorize("hasRole('admin') or hasRole('user')")
    public ResponseEntity<Void> cancelLeave(
            @PathVariable Long leaveId,
            @AuthenticationPrincipal Jwt jwt) {
        if (!securityService.hasLeaveAccess(leaveId, jwt)) {
            throw new AccessDeniedException("You don't have permission to cancel this leave");
        }
        leaveService.cancelLeave(leaveId);
        return ResponseEntity.noContent().build();
    }
}