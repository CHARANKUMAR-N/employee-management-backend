package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityService {

	private final EmployeeService employeeService;

	public SecurityService(EmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	public boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
			List<String> roles = jwt.getClaimAsStringList("https://api.employeemanagement.com/roles");
			return roles != null && roles.contains("admin");
		}
		return false;
	}

	public void validateEmployeeAccess(Long employeeId, Jwt jwt) {
		if (isAdmin()) {
			return;
		}

		String userEmail = jwt.getClaimAsString("email");
		if (userEmail == null) {
			userEmail = jwt.getClaimAsString("https://api.employeemanagement.com/user_email");
		}

		if (userEmail == null) {
			String subject = jwt.getSubject();
			if (subject != null && subject.contains("@")) {
				userEmail = subject;
			} else {
				throw new AccessDeniedException("Unable to identify user - no email available in token");
			}
		}

		EmployeeDTO employee = employeeService.getEmployeeById(employeeId);

		if (!userEmail.equals(employee.getEmail()) && !userEmail.equals(employee.getPersonalEmail())) {
			throw new AccessDeniedException("You can only access your own employee record");
		}
	}

	public void validateEmployeeAccess(EmployeeDTO employee, Jwt jwt) {
		if (isAdmin()) {
			return; // Admins have full access
		}

		String userEmail = jwt.getSubject();
		if (!userEmail.equals(employee.getEmail()) && !userEmail.equals(employee.getPersonalEmail())) {
			throw new AccessDeniedException("You can only access your own employee record");
		}
	}
}