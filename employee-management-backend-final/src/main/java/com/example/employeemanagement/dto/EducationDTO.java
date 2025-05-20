package com.example.employeemanagement.dto;
 
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EducationDTO {
    private Long educationId;
    private Long version;
    
    @NotBlank(message = "Education name is required")
    private String educationName;
    
    @NotBlank(message = "College is required")
    private String college;
    
    @NotBlank(message = "Year is required")
    private String year;
    
    @NotBlank(message = "Percentage is required")
    private String percentage;
}
