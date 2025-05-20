package com.example.employeemanagement.config;
 
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.dto.EmployeeDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
 
@Configuration
public class ModelMapperConfig {
 
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setCollectionsMergeEnabled(false);  // This is important
        
        modelMapper.typeMap(EmployeeDTO.class, Employee.class)
            .addMappings(mapper -> {
                mapper.skip(Employee::setEducationList);
                mapper.skip(Employee::setCertifications);
                mapper.skip(Employee::setSkills);
            });
        return modelMapper;
    }
}
