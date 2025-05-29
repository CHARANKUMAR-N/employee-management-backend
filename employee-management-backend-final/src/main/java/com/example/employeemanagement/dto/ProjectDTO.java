package com.example.employeemanagement.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProjectDTO {
    private Long id;
    private String name;
    private Long seniorProjectManagerId;
    private String seniorProjectManagerName;
    private List<TeamDTO> teams;
    private int teamsCount;
}
