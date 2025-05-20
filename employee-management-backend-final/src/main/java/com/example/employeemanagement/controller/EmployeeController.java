package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.*;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.service.*;
import com.itextpdf.text.DocumentException;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

	private final EmployeeService employeeService;
	private final PdfService pdfService;
	private final ProfilePhotoService profilePhotoService;
	private final DocumentService documentService;
	private final SecurityService securityService;

	public EmployeeController(EmployeeService employeeService, PdfService pdfService,
			ProfilePhotoService profilePhotoService, DocumentService documentService, SecurityService securityService) {
		this.employeeService = employeeService;
		this.pdfService = pdfService;
		this.profilePhotoService = profilePhotoService;
		this.documentService = documentService;
		this.securityService = securityService;
	}

	@GetMapping
	@PreAuthorize("hasRole('admin') or hasRole('user')")
	public ResponseEntity<List<EmployeeDTO>> getAllEmployees(@AuthenticationPrincipal Jwt jwt) {
		if (securityService.isAdmin()) {
			return ResponseEntity.ok(employeeService.getAllEmployees());
		} else {
			// Try to get email from standard claim first
			String userEmail = jwt.getClaimAsString("email");

			// Fallback to custom claim if standard email not found
			if (userEmail == null) {
				userEmail = jwt.getClaimAsString("https://api.employeemanagement.com/user_email");
			}

			// Final fallback to subject if still not found
			if (userEmail == null) {
				String subject = jwt.getSubject();
				if (subject != null && subject.contains("@")) {
					userEmail = subject;
				} else {
					throw new AccessDeniedException("Unable to identify user - no email available in token");
				}
			}

			EmployeeDTO employee = employeeService.getEmployeeByEmail(userEmail);
			if (employee == null) {
				throw new ResourceNotFoundException("Employee not found for email: " + userEmail);
			}

			return ResponseEntity.ok(List.of(employee));
		}
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasRole('admin') or hasRole('user')")
	public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
		EmployeeDTO employee = employeeService.getEmployeeById(id);

		// Check access
		if (!securityService.isAdmin()) {
			String userEmail = jwt.getSubject();
			if (!userEmail.equals(employee.getEmail()) && !userEmail.equals(employee.getPersonalEmail())) {
				throw new AccessDeniedException("You can only access your own employee record");
			}
		}

		return ResponseEntity.ok(employee);
	}

	@PostMapping
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<EmployeeDTO> saveEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
		if (employeeDTO.getProfilePhoto() != null) {
			employeeDTO.setProfilePhoto(null);
		}
		EmployeeDTO savedEmployee = employeeService.saveEmployee(employeeDTO);
		return ResponseEntity.ok(savedEmployee);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<EmployeeDTO> updateEmployee(@PathVariable Long id,
			@Valid @RequestBody EmployeeDTO employeeDTO) {
		EmployeeDTO updatedEmployee = employeeService.updateEmployee(id, employeeDTO);
		return ResponseEntity.ok(updatedEmployee);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
		employeeService.deleteEmployee(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}/pdf")
	@PreAuthorize("hasRole('admin') or hasRole('user')")
	public ResponseEntity<byte[]> downloadEmployeePdf(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
		try {
			EmployeeDTO employee = employeeService.getEmployeeById(id);

			// Check access
			if (!securityService.isAdmin()) {
				// Get email from multiple possible claims
				String userEmail = jwt.getClaimAsString("email");
				if (userEmail == null) {
					userEmail = jwt.getClaimAsString("https://api.employeemanagement.com/user_email");
				}

				if (userEmail == null) {
					String subject = jwt.getSubject();
					if (subject != null && subject.contains("@")) {
						userEmail = subject;
					}
				}

				if (userEmail == null
						|| (!userEmail.equals(employee.getEmail()) && !userEmail.equals(employee.getPersonalEmail()))) {
					throw new AccessDeniedException("You can only download your own PDF");
				}
			}

			byte[] pdfBytes = pdfService.generateEmployeePdf(employee);
			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"employee_" + id + ".pdf\"")
					.body(pdfBytes);
		} catch (DocumentException e) {
			throw new RuntimeException("Failed to generate PDF", e);
		}
	}

	@PostMapping("/{employeeId}/documents")
	@PreAuthorize("hasRole('admin')")
	public ResponseEntity<DocumentDTO> uploadDocument(@PathVariable Long employeeId,
			@RequestParam("file") MultipartFile file, @RequestParam("documentType") String documentType) {
		DocumentDTO documentDTO = documentService.uploadDocument(employeeId, file, documentType);
		return ResponseEntity.ok(documentDTO);
	}
}