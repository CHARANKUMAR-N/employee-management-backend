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
public class SkillDTO {
    private Long skillId;
    private Long version;

    
    @NotBlank(message = "Skill is required")
    private String skill;
}

