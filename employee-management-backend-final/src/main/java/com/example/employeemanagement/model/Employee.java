package com.example.employeemanagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "employee_id")
	private Long employeeId;

	private String firstName;
	private String lastName;
	private String gender;
	private LocalDate dob;

	@Column(unique = true)
	private String email;

	@Column(unique = true)
	private String personalEmail;

	private String fatherName;

	@Column(unique = true)
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
	
	@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.MEMBER; 

	@ManyToOne
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne
	@JoinColumn(name = "team_id")
	private Team team;

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Education> educationList = new ArrayList<>();

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Certification> certifications = new ArrayList<>();

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Skill> skills = new ArrayList<>();

	@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private ProfilePhoto profilePhoto;

	@OneToMany(mappedBy = "employee", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = false)
	private List<Document> documents = new ArrayList<>();

	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Experience> experiences = new ArrayList<>();

	// Helper methods for managing relationships
	public void addEducation(Education education) {
		educationList.add(education);
		education.setEmployee(this);
	}

	public void removeEducation(Education education) {
		educationList.remove(education);
		education.setEmployee(null);
	}

	public void addCertification(Certification certification) {
		certifications.add(certification);
		certification.setEmployee(this);
	}

	public void removeCertification(Certification certification) {
		certifications.remove(certification);
		certification.setEmployee(null);
	}

	public void addSkill(Skill skill) {
		skills.add(skill);
		skill.setEmployee(this);
	}

	public void removeSkill(Skill skill) {
		skills.remove(skill);
		skill.setEmployee(null);
	}

	public void setProfilePhoto(ProfilePhoto profilePhoto) {
		if (profilePhoto == null) {
			if (this.profilePhoto != null) {
				this.profilePhoto.setEmployee(null);
			}
		} else {
			profilePhoto.setEmployee(this);
		}
		this.profilePhoto = profilePhoto;
	}

	// Helper method for managing documents
	public void addDocument(Document document) {
		documents.add(document);
		document.setEmployee(this);
	}

	public void removeDocument(Document document) {
		documents.remove(document);
		document.setEmployee(null);
	}

	public void setDocuments(List<Document> documents) {
		if (this.documents == null) {
			this.documents = documents;
		} else {
			this.documents.clear();
			if (documents != null) {
				documents.forEach(this::addDocument);
			}
		}
	}

	public void addExperience(Experience experience) {
		experiences.add(experience);
		experience.setEmployee(this);
	}

	public void removeExperience(Experience experience) {
		experiences.remove(experience);
		experience.setEmployee(null);
	}

	public void setExperiences(List<Experience> experiences) {
		if (this.experiences == null) {
			this.experiences = new ArrayList<>();
		}
		this.experiences.clear();
		if (experiences != null) {
			for (Experience exp : experiences) {
				this.addExperience(exp); // ensures exp.setEmployee(this)
			}
		}
	}
}
