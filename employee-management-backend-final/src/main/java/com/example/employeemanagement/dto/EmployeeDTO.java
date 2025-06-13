package com.example.employeemanagement.dto;

import com.example.employeemanagement.model.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
	private Long employeeId;
	private String firstName;
	private String lastName;
	private String gender;
	private LocalDate dob;
	private String email;
	private String personalEmail;
	private String fatherName;
	private String mobile;

	// Present Address
	private String presentStreet;
	private String presentCity;
	private String presentState;
	private String presentZip;

	// Permanent Address
	private String permanentStreet;
	private String permanentCity;
	private String permanentState;
	private String permanentZip;

	private Role role;
	private Long projectId;
	private Long teamId;

	private ProfilePhotoDTO profilePhoto;
	private List<EducationDTO> educationList = new ArrayList<>();
	private List<CertificationDTO> certifications = new ArrayList<>();
	private List<SkillDTO> skills = new ArrayList<>();
	private List<DocumentDTO> documents = new ArrayList<>();
	private List<ExperienceDTO> experiences = new ArrayList<>();
	private List<LeaveDTO> leaves = new ArrayList<>();

	// For document deletion
	private List<Long> documentsToDelete;
	private Boolean removeProfilePhoto;

	// Helper method to get full name
	public String getFullName() {
		return firstName + " " + lastName;
	}
}
