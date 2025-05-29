package com.example.employeemanagement.dto;
 
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.example.employeemanagement.model.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDTO {
    private Long employeeId;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Gender is required")
    private String gender;
    
    @NotNull(message = "Date of birth is required")
    private LocalDate dob;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Personal email is required")
    @Email(message = "Invalid personal email format")
    private String personalEmail;
    
    @NotBlank(message = "Father's name is required")
    private String fatherName;
    
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "\\d{10}", message = "Mobile number must be 10 digits")
    private String mobile;
    
    // Present Address
    @NotBlank(message = "Present street is required")
    private String presentStreet;
    
    @NotBlank(message = "Present city is required")
    private String presentCity;
    
    @NotBlank(message = "Present state is required")
    private String presentState;
    
    @NotBlank(message = "Present zip code is required")
    private String presentZip;
    
    // Permanent Address
    @NotBlank(message = "Permanent street is required")
    private String permanentStreet;
    
    @NotBlank(message = "Permanent city is required")
    private String permanentCity;
    
    @NotBlank(message = "Permanent state is required")
    private String permanentState;
    
    @NotBlank(message = "Permanent zip code is required")
    private String permanentZip;
    

    @NotNull(message = "Role is required")
    private Role role;

    
   
    private List<EducationDTO> educationList = new ArrayList<>();
    private List<CertificationDTO> certifications = new ArrayList<>();
    private List<SkillDTO> skills = new ArrayList<>();
    
    private List<DocumentDTO> documents = new ArrayList<>();
    private List<Long> documentsToDelete;
    
    private List<ExperienceDTO> experiences = new ArrayList<>();

    
    private ProfilePhotoDTO profilePhoto;
    private Boolean removeProfilePhoto;
    
    private Long projectId;
    private String projectName;
    private Long teamId;
    private String teamName;

    
    // Add this method to help with photo handling
   
    
    public boolean hasPhotoData() {
        return this.profilePhoto != null && this.profilePhoto.getData() != null;
    }
}


