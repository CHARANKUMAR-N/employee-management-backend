package com.example.employeemanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @ManyToOne
    @JoinColumn(name = "senior_project_manager_id")
    private Employee seniorProjectManager;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Team> teams = new ArrayList<>();
    
    // Helper methods
    public void addTeam(Team team) {
        teams.add(team);
        team.setProject(this);
    }
    
    public void removeTeam(Team team) {
        teams.remove(team);
        team.setProject(null);
    }
}
