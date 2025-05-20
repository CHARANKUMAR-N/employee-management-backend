package com.example.employeemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "experiences")
@Getter
@Setter
public class Experience {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "experience_id")
	private Long experienceId;

	@Column(nullable = false)
	private String level; // Level1, Level2, etc.

	@Column(nullable = false)
	private String jobRole;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	@Version
	private Long version;
}
