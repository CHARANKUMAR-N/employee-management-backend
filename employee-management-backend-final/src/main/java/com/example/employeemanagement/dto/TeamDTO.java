package com.example.employeemanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TeamDTO {
    private Long id;
    private String name;
    private Long projectId;
    private String projectName;
    private Long projectManagerId;
    private String projectManagerName;
    private Long teamManagerId;
    private String teamManagerName;
    private List<EmployeeDTO> members;
    
    // Constructor for convenience
    public TeamDTO() {}
}