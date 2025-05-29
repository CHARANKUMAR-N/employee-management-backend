package com.example.employeemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
@Getter
@Setter
public class Team {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@ManyToOne
    @JoinColumn(name = "project_manager_id")
    private Employee projectManager;

	@ManyToOne
	@JoinColumn(name = "team_manager_id")
	private Employee teamManager;

	@OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Employee> members = new ArrayList<>();

	// Helper methods
	public void addMember(Employee employee) {
		if (members.size() >= 6) {
			throw new IllegalStateException("A team can have at most 6 members");
		}
		members.add(employee);
		employee.setTeam(this);
	}

	public void removeMember(Employee employee) {
		members.remove(employee);
		employee.setTeam(null);
	}
}
