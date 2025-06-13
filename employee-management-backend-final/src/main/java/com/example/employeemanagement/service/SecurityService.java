package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.LeaveDTO;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.repository.EmployeeRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityService {

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;
    private final LeaveService leaveService;

    public SecurityService(EmployeeService employeeService, 
                         EmployeeRepository employeeRepository,
                         LeaveService leaveService) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
        this.leaveService = leaveService;
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            List<String> roles = jwt.getClaimAsStringList("https://api.employeemanagement.com/roles");
            return roles != null && roles.contains("admin");
        }
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_admin"));
    }

    public String getUserEmailFromJwt(Jwt jwt) {
        if (jwt == null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
                jwt = (Jwt) authentication.getPrincipal();
            } else {
                throw new AccessDeniedException("No authentication information available");
            }
        }

        String userEmail = jwt.getClaimAsString("email");
        if (userEmail == null) {
            userEmail = jwt.getClaimAsString("https://api.employeemanagement.com/user_email");
        }
        if (userEmail == null) {
            String subject = jwt.getSubject();
            if (subject != null && subject.contains("@")) {
                userEmail = subject;
            } else if (subject != null && subject.startsWith("auth0|")) {
                userEmail = subject.substring(subject.indexOf("|") + 1);
            } else {
                throw new AccessDeniedException("Unable to identify user - no email available in token");
            }
        }
        return userEmail;
    }

    public void validateEmployeeAccess(Long employeeId, Jwt jwt) {
        if (isAdmin()) {
            return;
        }

        String userEmail = getUserEmailFromJwt(jwt);
        EmployeeDTO employee = employeeService.getEmployeeById(employeeId);

        if (!userEmail.equals(employee.getEmail()) && !userEmail.equals(employee.getPersonalEmail())) {
            throw new AccessDeniedException("You can only access your own employee record.");
        }
    }

    public void validateEmployeeAccess(EmployeeDTO employee, Jwt jwt) {
        if (isAdmin()) {
            return;
        }

        String userEmail = getUserEmailFromJwt(jwt);
        if (!userEmail.equals(employee.getEmail()) && !userEmail.equals(employee.getPersonalEmail())) {
            throw new AccessDeniedException("You can only access your own employee record.");
        }
    }

    public Long getEmployeeIdFromToken(Jwt jwt) {
        String userEmail = getUserEmailFromJwt(jwt);
        return employeeRepository.findByEmail(userEmail)
                .or(() -> employeeRepository.findByPersonalEmail(userEmail))
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for email: " + userEmail))
                .getEmployeeId();
    }

    public boolean hasLeaveAccess(Long leaveId, Jwt jwt) {
        if (isAdmin()) {
            return true;
        }
        
        LeaveDTO leave = leaveService.getLeaveById(leaveId);
        Long currentEmployeeId = getEmployeeIdFromToken(jwt);
        
        return currentEmployeeId.equals(leave.getEmployeeId());
    }
}
