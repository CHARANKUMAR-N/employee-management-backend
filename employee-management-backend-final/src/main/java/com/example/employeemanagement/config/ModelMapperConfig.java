package com.example.employeemanagement.config;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.ProjectDTO;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.model.Project;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        
        // General configuration
        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setSkipNullEnabled(true)
            .setCollectionsMergeEnabled(false);

        // Project -> ProjectDTO mapping
        modelMapper.createTypeMap(Project.class, ProjectDTO.class)
            .addMappings(mapper -> {
                mapper.<Long>map(src -> src.getSeniorProjectManager() != null ? 
                    src.getSeniorProjectManager().getEmployeeId() : null,
                    ProjectDTO::setSeniorProjectManagerId);
                mapper.<String>map(src -> src.getSeniorProjectManager() != null ? 
                    src.getSeniorProjectManager().getFirstName() + " " + 
                    src.getSeniorProjectManager().getLastName() : null,
                    ProjectDTO::setSeniorProjectManagerName);
            });

        // ProjectDTO -> Project mapping
        modelMapper.createTypeMap(ProjectDTO.class, Project.class)
            .addMappings(mapper -> {
                mapper.skip(Project::setSeniorProjectManager);
                mapper.skip(Project::setTeams); // Keep this skip as it was in your original code
            });

        // Employee mappings
        modelMapper.createTypeMap(EmployeeDTO.class, Employee.class)
            .addMappings(mapper -> {
                mapper.skip(Employee::setEducationList);
                mapper.skip(Employee::setCertifications);
                mapper.skip(Employee::setSkills);
                mapper.skip(Employee::setProject);
                mapper.skip(Employee::setTeam);
            });

        return modelMapper;
    }
}
